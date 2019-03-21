package com.troy.redis.sentinel.controller;

import com.troy.redis.sentinel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    
//    private ValueOperations<Object,Object> valueOperations;
//    private ListOperations<Object,Object> listOperations;
//    private HashOperations hashOperations;
//
//    @PostConstruct
//    public void getValueOperation(){
//        valueOperations = redisTemplate.opsForValue();
//        listOperations = redisTemplate.opsForList();
//        hashOperations = redisTemplate.opsForHash();
//    }    

    @RequestMapping("/get/{key}")
    public String getValue(@PathVariable("key") String key){
        System.out.println("get key from redis key is " + key);
        ValueOperations<String,String> stringRedis = stringRedisTemplate.opsForValue();
        String value = stringRedis.get(key);
        System.out.println("key="+key+" value is " + value);
        return value;
    }

    @RequestMapping("/getList/{key}")
    public String getList(@PathVariable("key") String key){
        System.out.println("start to get key from redis key is " + key);
        ListOperations options = redisTemplate.opsForList();
        List<User> nUsers = options.range(key,0,-1);
        System.out.println("show List<User> value from redis");
        nUsers.forEach(f->{
            System.out.println(f.toString());
        });
        String value = key+"~"+nUsers.size();
        return value;
    }

    @RequestMapping("/getMaps/{key}")
    public String getMaps(@PathVariable("key") String key){
        System.out.println("start to get key from redis key is " + key);
        HashOperations options = redisTemplate.opsForHash();
        Map nUsers = redisTemplate.opsForHash().entries(key);
        System.out.println("get all Hash from redis, key is " + key);
        return key +" has " + nUsers.size()+" total items";
    }

    @RequestMapping("/getMapObj/{key}/{entryKey}")
    public String getMapObj(@PathVariable("key") String key,@PathVariable("entryKey") String entryKey){
        System.out.println("start to get key from redis key is " + key);
        HashOperations options = redisTemplate.opsForHash();
        Map nUsers = redisTemplate.opsForHash().entries(key);
        System.out.println("get all Hash from redis, key is " + key);
        System.out.println(nUsers.values().toString());
        return nUsers.values().toString();
    }

    @RequestMapping("/set/{key}/{value}")
    public String setValue(@PathVariable("key") String key,@PathVariable("value") String value){
        System.out.println("get key from redis key is " + key);
        ValueOperations<String,String> stringRedis = stringRedisTemplate.opsForValue();
        stringRedis.set(key,value);
        String val = stringRedis.get(key);
        System.out.println("key="+key+" value is " + val);
        return val;
    }

    @RequestMapping("/add/{name}/{age}/{id}")
    public String addUser(@PathVariable("name") String name, @PathVariable("age") String age, @PathVariable("id") int id){
        System.out.println("user name="+name+", age="+age);
        redisTemplate.opsForValue().set("user"+id,new User(id,name,age));
        User user = (User)redisTemplate.opsForValue().get("user"+id);
        System.out.println(user.toString());
        return user.toString();
    }

    @RequestMapping("/addList/{keyIndex}")
    public String addList(@PathVariable("keyIndex") String keyIndex){
        String name = "troy~"+keyIndex;
        String age = keyIndex;
        int startId = Integer.parseInt(keyIndex);
        int endId = startId + 3;
        List<User> users = new ArrayList<>();
        for(int id=startId; id<endId;id++){
            System.out.println("user name="+name+", age="+id+", id="+id);
            users.add(new User(id,name,age));
        }
        String listKey = "user~"+keyIndex+"-"+endId;
        ListOperations options = redisTemplate.opsForList();
        options.leftPushAll(listKey,users);
        List<User> nUsers = options.range(listKey,0,-1);
        System.out.println("show List<User> value from redis");
        nUsers.forEach(f->{
            System.out.println(f.toString());
        });
        String returnValue = "success key is "+listKey;
        return returnValue;
    }

    @RequestMapping("/addMap/{keyIndex}")
    public String addMap(@PathVariable("keyIndex") String keyIndex){
        int startId = Integer.parseInt(keyIndex);
        int endId = startId + 3;
        Map<String, List<User>> map =  new HashMap<String, List<User>>();
        List<User> users = new ArrayList<>();
        for(int id=startId; id<endId;id++){
            StringBuffer name = new StringBuffer("troy~");
            name.append(id);
            System.out.println("user name="+name.toString()+", age="+id+", id="+id);
            users.add(new User(id,name.toString(),id+""));
        }
        String mapKey = "user~"+keyIndex+"-"+endId;
        map.put(mapKey,users);
        HashOperations options = redisTemplate.opsForHash();
        options.putAll("map=>"+mapKey,map);

        Map nUsers = redisTemplate.opsForHash().entries("map=>"+mapKey);
        System.out.println("get all Hash from redis, key is " + "map=>"+mapKey);
        Object obj = nUsers.get(mapKey);
        System.out.println("Get HashMap Value, key is " + mapKey);
        System.out.println(obj.toString());
        System.out.println("show List<User> from redis Hash");
        return "success key is "+"map=>"+mapKey;
    }
}
