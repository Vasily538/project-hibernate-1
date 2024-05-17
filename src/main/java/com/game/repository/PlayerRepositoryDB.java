package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "Point007");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(com.game.entity.Player.class)
                .buildSessionFactory();

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {

        Session session = sessionFactory.openSession();

        Query<Player> query = session.createNativeQuery("select * from player ", Player.class)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);

        List<Player> resultList = query.list();
        return resultList;
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(Player.Player_Get_All_Count);
        Long count = (Long)query.uniqueResult();
        return count.intValue();
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(player);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        Player result = null;
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            result = (Player) session.merge(player);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        }
        return result;
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> result;
        Session session = sessionFactory.openSession();
        result = Optional.ofNullable((Player) session.get(Player.class, id));
        return result;
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.remove(player);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}