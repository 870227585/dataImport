package com.bdi.sselab.repository.userDepart;

import com.bdi.sselab.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by wh on 2019/3/25.
 */
@Service
public class RepositoriesInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Object lock = new Object();
    private boolean initialized = false;
    public void initialize(){
        if (!initialized){
            synchronized(lock){
                initializeAll();
            }
        }
}
    private void initializeAll(){
        initializeUsers();
    }

    private void initializeUsers(){
        if (userRepository.count() > 5) {
            return;
        }
        User user = new User();
        User user1 = new User();
        User user3 = new User();
        User user4 = new User();
        User user5 = new User();
        User user6 = new User();
        User user7 = new User();
        user.setUsername("root");
        user1.setUsername("wuyizhongxue");
        user3.setUsername("wuyizhijiao");
        user4.setUsername("agriculture");
        user5.setUsername("jiaoyuju");
        user6.setUsername("economy");
        user7.setUsername("medicalTreatment");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user3.setPassword(bCryptPasswordEncoder.encode("123456"));
        user4.setPassword(bCryptPasswordEncoder.encode("123456"));
        user5.setPassword(bCryptPasswordEncoder.encode("123456"));
        user6.setPassword(bCryptPasswordEncoder.encode("123456"));
        user7.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setRoler("admin");
        user1.setRoler("wuyizhongxue");
        user3.setRoler("wuyizhijiao");
        user4.setRoler("agriculture");
        user5.setRoler("jiaoyuju");
        user6.setRoler("economy");
        user7.setRoler("medicalTreatment");
        userRepository.save(user);
        userRepository.save(user1);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);

    }

}
