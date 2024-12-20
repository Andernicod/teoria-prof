package br.mackenzie.webapp.security.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.mackenzie.webapp.security.dao.UserDao;
import br.mackenzie.webapp.security.model.DAOUser;
import br.mackenzie.webapp.security.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	public DAOUser save(UserDTO userDTO) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(userDTO.getUsername());
		newUser.setPassword(bcryptEncoder.encode(userDTO.getPassword()));
		newUser.setNome(userDTO.getNome());
		newUser.setSobrenome(userDTO.getSobrenome());
		newUser.setEmail(userDTO.getEmail());
		return userDao.save(newUser);
	}

	public DAOUser findByUsername(String username) {
		return userDao.findByUsername(username);
	}
}