package hello;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RestController
public class MyController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/users")
    public Iterable<User> getUsers(){
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
    }

    @GetMapping("/")
    public String hello(){
        return "Hello Sweetie";
    }
    @PostMapping("/users")
    public User createUser(@RequestBody User user){
        User createdUser = userService.saveUser(user);
        return createdUser;
    }

   @PutMapping("/users/{id}")
    public User edit(@PathVariable("id") Long id, @RequestBody User formData) throws Exception{
        Optional<User> response = userRepository.findById(id);
        if(response.isPresent()){
            User user = response.get();
            user.setEmail(formData.getEmail());
            user.setPassword(formData.getPassword());
            return userRepository.save(user);
        }
        throw new Exception("no such post");
   }

    @GetMapping("/users/{id}")
    public User getUser (@PathVariable("id") Long id) throws Exception{
        Optional<User> response = userRepository.findById(id);
        if(response.isPresent()){
            return response.get();
        }
        throw new Exception("no such post");
    }

    @PostMapping("/auth/login")
    public User login(@RequestBody User login, HttpSession session) throws IOException {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findByEmail(login.getEmail());
        if(user ==  null){
            throw new IOException("Invalid Credentials");
        }
        boolean valid = bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword());
        if(valid){
            session.setAttribute("email", user.getEmail());
            return user;
        }else{
            throw new IOException("Invalid Credentials");
        }
    }
}

