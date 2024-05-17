package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
@org.hibernate.annotations.NamedQuery(
        name = "Player_GetAllCount",
        query = "select count(*) from Player"
)
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration()
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {

        return sessionFactory.openSession()
                .createNativeQuery("select * from player ", Player.class)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    public int getAllCount() {
        return sessionFactory.openSession().createNamedQuery("Player_GetAllCount", Integer.class)
                .uniqueResult();
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