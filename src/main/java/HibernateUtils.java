import com.discordbot.teekanne.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtils {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    protected static void createSessionFactory(){
        if (sessionFactory == null){
            try{
                registry = new StandardServiceRegistryBuilder().configure().build();

                MetadataSources sources = new MetadataSources(registry);

                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e){
                e.printStackTrace();
                if(registry != null){
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
    }

    protected static void handleJoinCommand(long id, String name){
        if(getUserById(id) == null){
            User user = new User(id, name);
            Transaction transaction = null;

            try(Session session = sessionFactory.openSession()){
                transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
            } catch (Exception e){
                if (transaction != null){
                    transaction.rollback();
                }
                e.printStackTrace();
            }

            //TODO: send message to user
        }else{
            System.out.println("User already exists.");
            //TODO: Send message to user
        }

    }

    protected static void handleLeaveCommand(long id) {
        User user = getUserById(id);
        if(user != null){
            Transaction transaction = null;
            try(Session session = sessionFactory.openSession()){
                transaction = session.beginTransaction();
                session.remove(user);
                transaction.commit();
            } catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //TODO: Send message to user
        }
    }

    protected static User getUserById(long id){
        User user;
        try {
            Session session = sessionFactory.openSession();
            user = session.get(User.class, id);

            if(user != null){
                return user;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void shutdown(){
        if (registry != null){
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
