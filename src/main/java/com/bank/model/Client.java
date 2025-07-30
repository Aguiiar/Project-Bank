package com.bank.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.bank.utils.MoneySerializer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;
	@NotBlank(message = "Name required")
	private String name;
	@NotNull(message = "Age required")
	private Integer age;
	@NotBlank(message = "Address required")
	private String address;
	@NotBlank(message = "Email required")
	private String email;
	@NotNull(message = "Salary required")
	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal salary;

	@JsonManagedReference
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)

	private java.util.List<Account> accounts = new ArrayList<>();

	@JsonManagedReference
	@OneToOne(mappedBy = "client", cascade = CascadeType.ALL)

	private UserLogin userLogin;

	public Client() {
		super();
	}

	public Client(Long id, String name, Integer age, String address, String email, BigDecimal salary) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.address = address;
		this.email = email;
		this.salary = salary;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public java.util.List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(java.util.List<Account> accounts) {
		this.accounts = accounts;
	}

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public void addAccount(Account account) {
		accounts.add(account);
		account.setClient(this);
	}

	public void removeAccount(Account account) {
		accounts.remove(account);
		account.setClient(null);
	}

}
