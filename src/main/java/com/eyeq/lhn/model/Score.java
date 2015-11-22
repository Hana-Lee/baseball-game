package com.eyeq.lhn.model;

import lombok.*;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2015-11-15 22:07
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Getter
public class Score implements Serializable {

	private static final long serialVersionUID = -9101473675515636509L;

	private long id;
	@Setter
	private String name;
	private int score;
	private boolean solved;
	private String created;
	private boolean enabled;
}
