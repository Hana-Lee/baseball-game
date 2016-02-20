package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.MimeTypeUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-02-20 18:46
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Avatar implements Serializable {

	private static final long serialVersionUID = -3010221624647714206L;

	@Id
	@GeneratedValue
	@Column(name = "avatar_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	private String imagePath = "images/blank_character_2.gif";
	private String imageName = "blank_character_2.gif";
	private String imageExt = "gif";
	private String mimeType = MimeTypeUtils.IMAGE_GIF_VALUE;
}
