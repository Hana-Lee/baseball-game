package com.eyeq.lhn.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Hana Lee
 * @since 2015-11-22 17:23
 */
@Data
@AllArgsConstructor
public class Menu {

	private long id;

	private String name;

	private String description;
}
