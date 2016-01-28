package kr.co.leehana.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 16:59
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private long id;

	@Column(unique = true)
	private String email;

	private String nickname;

	@JsonIgnore
	private String password;

	@OneToOne
	private Level level;
	@OneToOne
	private Rank totalRank;
	@OneToOne
	private MatchRecord matchRecord;

	@Temporal(TemporalType.TIMESTAMP)
	private Date joined;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	private boolean admin;
}
