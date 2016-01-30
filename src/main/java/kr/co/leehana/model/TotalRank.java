package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-30 17:15
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class TotalRank implements Serializable {

	private static final long serialVersionUID = 7794090430292206635L;

	@Id
	@GeneratedValue
	@Column(name = "total_rank_id")
	@Setter(AccessLevel.NONE)
	private long id;

	private int value;

	public TotalRank(int value) {
		this.value = value;
	}
}
