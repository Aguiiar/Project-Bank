package com.bank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Client;
import com.bank.model.UserLogin;
import com.bank.repository.ClientRepository;
import com.bank.repository.UserLoginRepository;

@Service
public class ClientService {

	private final ClientRepository clientRepository;

	private final UserLoginRepository userLoginRepository;

	public ClientService(ClientRepository clientRepository, UserLoginRepository userLoginRepository) {
		this.clientRepository = clientRepository;
		this.userLoginRepository = userLoginRepository;
	}

	public Client save(Client client) {
		return clientRepository.save(client);

	}

	public List<Client> list() {
		return clientRepository.findAll();

	}

	public Client findById(Long id) {
		return clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

	}

	public Client updateClient(Long id, Client updatedClient) {
		Client existingUser = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid update - Client not found with id: " + id));

		String oldEmail = existingUser.getEmail();

		existingUser.setName(updatedClient.getName());
		existingUser.setAge(updatedClient.getAge());
		existingUser.setEmail(updatedClient.getEmail());
		existingUser.setAddress(updatedClient.getAddress());
		existingUser.setSalary(updatedClient.getSalary());

		if (!oldEmail.equals(updatedClient.getEmail())) {
			UserLogin userLogin = userLoginRepository.findByEmail(oldEmail)
					.orElseThrow(() -> new ResourceNotFoundException("User login not found with email: " + oldEmail));

			userLogin.setEmail(updatedClient.getEmail());
			userLoginRepository.save(userLogin);

		}

		return clientRepository.save(existingUser);

	}

	public void deleteClient(Long id) {

		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid delete - Client not found with id: " + id));

		clientRepository.delete(client);

	}
}
