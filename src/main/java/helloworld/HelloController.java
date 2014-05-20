package helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
    @Autowired(required=false) DataSource dataSource;
    @Autowired(required=false) RedisConnectionFactory redisConnectionFactory;
    @Autowired(required=false) MongoDbFactory mongoDbFactory;
    @Autowired(required=false) ConnectionFactory rabbitConnectionFactory;
    
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
        for (Map.Entry<Object, Object> property : System.getProperties().entrySet()) {
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
            if (ClassUtils.isPresent("org.apache.tomcat.dbcp.dbcp.BasicDataSource", ClassUtils.getDefaultClassLoader())
                    && dataSource instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource) {
                return ((org.apache.tomcat.dbcp.dbcp.BasicDataSource) dataSource).getUrl();
            } else if (ClassUtils.isPresent("org.apache.commons.dbcp2.BasicDataSource", ClassUtils.getDefaultClassLoader())
                    && dataSource instanceof org.apache.commons.dbcp2.BasicDataSource) {
                return ((org.apache.commons.dbcp2.BasicDataSource) dataSource).getUrl();
            } else if (dataSource instanceof SimpleDriverDataSource) {
                return ((SimpleDriverDataSource) dataSource).getUrl();
            } else {
                return "<unknown>";
            }
        }
    }
    
    private String toString(MongoDbFactory mongoDbFactory) {
        if (mongoDbFactory == null) {
            return "<none>";
        } else {
             return mongoDbFactory.getDb().getMongo().getAddress().toString();
        }
    }
    
    private String toString(RedisConnectionFactory redisConnectionFactory) {
        if (redisConnectionFactory == null) {
            return "<none>";            
        } else {
            return ((JedisConnectionFactory) redisConnectionFactory).getHostName().toString() + ":" + 
                   ((JedisConnectionFactory) redisConnectionFactory).getPort();
        }

    }
    
    private String toString(ConnectionFactory rabbitConnectionFactory) {
        if (rabbitConnectionFactory == null) {
            return "<none>";            
        } else {
            return rabbitConnectionFactory.getHost() + ":" + rabbitConnectionFactory.getPort();
        }

    }
}
