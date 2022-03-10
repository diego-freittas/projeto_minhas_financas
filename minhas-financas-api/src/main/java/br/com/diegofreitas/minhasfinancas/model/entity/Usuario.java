package br.com.diegofreitas.minhasfinancas.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuario", schema = "financas")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {


//	public Usuario(String nome, String email) {
//		super();
//		this.nome = nome;
//		this.email = email;
//	}

	@Id()
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "senha")
	@JsonIgnore
	private String senha;


}
