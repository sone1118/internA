package com.contentree.interna.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "joins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Joins {
	@Id
	@Column(name = "user_seq")
	private Long userSeq;
	
	@Column(name = "joins_id")
	private String joinsId;

	@Builder
	public Joins(Long userSeq, String joinsId) {
		this.userSeq = userSeq;
		this.joinsId = joinsId;
	}
}
