package com.devsuperior.workshopmongo.services;

import com.devsuperior.workshopmongo.dto.UserDTO;
import com.devsuperior.workshopmongo.entities.User;
import com.devsuperior.workshopmongo.repositories.UserRepository;
import com.devsuperior.workshopmongo.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public Flux<UserDTO> findAll() {
		return repository.findAll().map(user -> new UserDTO(user));
	}

	public Mono<UserDTO> findById(String id){
		return repository.findById(id)
				.map(user -> new UserDTO(user))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado!")));
	}

	public Mono<UserDTO> insert(UserDTO dto){
		User entity = new User();
		this.copyDtoToEntity(dto, entity);
		Mono<UserDTO> result = repository.save(entity).map(user -> new UserDTO(user));
		return result;
	}

	public Mono<UserDTO> update(String id, UserDTO userDTO){
		return repository.findById(id).flatMap(existingUser -> {
			existingUser.setEmail(userDTO.getEmail());
			existingUser.setName(userDTO.getName());
			return this.repository.save(existingUser);
		}).map(user -> new UserDTO(user))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado!")));
	}

	public Mono<Void> delete(String id){
		return repository.findById(id).flatMap(usuario -> {
			return this.repository.delete(usuario);
		}).switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado!")));
	}
	private void copyDtoToEntity(UserDTO dto, User entity){
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
	}


}