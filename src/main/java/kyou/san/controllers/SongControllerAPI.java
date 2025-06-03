package kyou.san.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import kyou.san.repository.SongRepository;

@RestController
@RequestMapping("/api/songs")
public class SongControllerAPI {
	@Autowired
	private SongRepository songRepository;

	@Value("${cloudinary.cloud-name")
	private String cloudName;

	@Value("${cloudinary.api-key")
	private String apiKey;

	@Value("${cloudinary.api-secret")
	private String apiSecret;

	private Cloudinary getCloudinary() {
		return new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret, "secure", true));
	}
	
	@GetMapping("/api/songs")
	public ResponseEntity<?> getSongs(@RequestParam(required = false) String language) {
	    if (language != null && !language.isEmpty()) {
	        return ResponseEntity.ok(songRepository.findByLanguage(language));
	    }
	    return ResponseEntity.ok(songRepository.findAll());
	}

	 @GetMapping("/list")
	    public ResponseEntity<?> listSongs() {
	        return ResponseEntity.ok(songRepository.findAll());
	 }
}
