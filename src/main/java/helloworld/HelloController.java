package helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
    @Autowired(required = false) DataSource dataSource;
    @Autowired(required = false) RedisConnectionFactory redisConnectionFactory;
    @Autowired(required = false) MongoDbFactory mongoDbFactory;
    @Autowired(required = false) ConnectionFactory rabbitConnectionFactory;

    @RequestMapping("/")
    public String hello(Model model) {
        System.out.println("Visiting /");
        Map<String, String> services = new LinkedHashMap<String, String>();
        services.put("Data Source", toString(dataSource));
        services.put("MongoDB", toString(mongoDbFactory));
        services.put("Redis", toString(redisConnectionFactory));
        services.put("RabbitMQ", toString(rabbitConnectionFactory));
        model.addAttribute("services", services.entrySet());
        return "home";
    }

    @RequestMapping("/env")
    public void env(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("System Properties:");
        for (Map.Entry<Object, Object> property : System.getProperties()
                .entrySet()) {
            out.println(property.getKey() + ": " + property.getValue());
        }
        out.println();
        out.println("System Environment:");
        for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
            out.println(envvar.getKey() + ": " + envvar.getValue());
        }
        out.println();
    }

    private String toString(DataSource dataSource) {
        if (dataSource == null) {
            return "<none>";
        } else {
            try {
                Field urlField = ReflectionUtils.findField(dataSource.getClass(), "url");
                ReflectionUtils.makeAccessible(urlField);
                return (String) urlField.get(dataSource);
            } catch (Exception fe) {
                try {
                    Method urlMethod = ReflectionUtils.findMethod(dataSource.getClass(), "getUrl");
                    ReflectionUtils.makeAccessible(urlMethod);
                    return (String) urlMethod.invoke(dataSource, (Object[])null);
                } catch (Exception me){
                    return "<unknown> " + dataSource.getClass();                    
                }
            }
        }
    }

    private String toString(MongoDbFactory mongoDbFactory) {
        if (mongoDbFactory == null) {
            return "<none>";
        } else {
            return mongoDbFactory.getDb().getMongo().toString();
        }
    }

    private String toString(RedisConnectionFactory redisConnectionFactory) {
        if (redisConnectionFactory == null) {
            return "<none>";
        } else {
            if (redisConnectionFactory instanceof JedisConnectionFactory) {
                JedisConnectionFactory jcf = (JedisConnectionFactory) redisConnectionFactory;
                return jcf.getHostName().toString() + ":" + jcf.getPort();
            } else if (redisConnectionFactory instanceof LettuceConnectionFactory) {
                LettuceConnectionFactory lcf = (LettuceConnectionFactory) redisConnectionFactory;
                return lcf.getHostName().toString() + ":" + lcf.getPort();
            }
            return "<unknown> " + redisConnectionFactory.getClass();
        }
    }

    private String toString(ConnectionFactory rabbitConnectionFactory) {
        if (rabbitConnectionFactory == null) {
            return "<none>";
        } else {
            return rabbitConnectionFactory.getHost() + ":"
                    + rabbitConnectionFactory.getPort();
        }
    }
}