package cn.sanleny.jt808.server.framework.handler.session;

import cn.sanleny.jt808.server.framework.handler.SessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: sanleny
 * @Date: 2019-12-11
 * @Version: 1.0
 */
@Service
public class SessionStoreServiceImpl implements SessionStoreService {

    private Map<String, SessionStore> sessionCache = new ConcurrentHashMap<>();

    private Map<String, String> connect = new ConcurrentHashMap<>();

    @Override
    public void put(String clientId, SessionStore sessionStore) {
        sessionCache.put(clientId, sessionStore);
    }

    @Override
    public SessionStore get(String clientId) {
        return sessionCache.get(clientId);
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionCache.containsKey(clientId);
    }

    @Override
    public void remove(String clientId) {
        if(this.containsKey(clientId)){
            sessionCache.remove(clientId);
        }
    }

    @Override
    public void connectClean(String id) {
        connect.remove(id);
    }

    @Override
    public void connectRemove(String id, String clientId) {
        if(connect.containsKey(id)){
            this.remove(connect.get(id));
        }
        connect.put(id,clientId);
    }

}
