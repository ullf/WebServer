package ru.marksblog;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class DB {

    SessionFactory factory;
    boolean isLogin=false;

    public DB(){
        factory=new Configuration().configure().buildSessionFactory();
    }

    public List<String> getColumn(String column){
        Session ses=factory.openSession();
        ses.beginTransaction();
        Query sql=ses.createQuery("select "+column+" from User");
        List<String> list;
        list=sql.list();
        return list;
    }

    public String getValue(String value){
        Session ses=factory.openSession();
        ses.beginTransaction();
        Query sql=ses.createQuery("select nickname from User where nickname = "+"'"+value+"'");
        List<String> list;
        list=sql.list();
        if(list.size()>0){
            return list.get(0);
        }
        else{
            return "null";
        }
    }

    public void setValue(String value){
        Session ses=factory.openSession();
        ses.beginTransaction();
        User user=new User();
        user.setNickname(value);
        ses.save(user);
        ses.getTransaction().commit();
    }
}
