package com.contentree.interna.user.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;

@Entity
public class BaseTime {

	private LocalDateTime createAt;
	private LocalDateTime updatedAt;

}
