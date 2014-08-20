package com.example;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

@Controller
@EnableAutoConfiguration
public class MainController {

  @RequestMapping("/")
  @ResponseBody
  String home() {
      return "Hello World!";
  }
  
  @RequestMapping(value="/submit/", method = RequestMethod.POST)
  @ResponseBody
  String submit(@RequestBody String body) {
    System.out.println("body => " + body);
    
    String[] args = body.split("&");
    
    for (String arg : args) {
      String[] parts = arg.split("=");
      String key = parts[0];
      String val = parts.length > 1 ? parts[1] : null;
      System.out.println(key + " => " + val);
    }
    
    try {
      Connection connection = getConnection();

      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      
    } catch (Exception e) {
      return "There was an error: " + e.getMessage();
    }

    return "ok";
  }

  private Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI(System.getenv("DATABASE_URL"));

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
  }

  public static void main(String[] args) throws Exception {
      SpringApplication.run(MainController.class, args);
  }
}
