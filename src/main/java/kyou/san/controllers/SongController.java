package kyou.san.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import kyou.san.models.Song;
import kyou.san.repository.SongRepository;

@Controller
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true
        ));
    }

    
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("language") String language,
                         RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".mp3")) {
            redirectAttributes.addFlashAttribute("error", "File không hợp lệ");
            return "redirect:/songs/home"; 
        }

        String publicId = language.trim().toLowerCase().replaceAll("[^a-z0-9-_]", "-")
                         + "-" + System.currentTimeMillis();
        
        Map<?, ?> uploadResult = getCloudinary().uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "resource_type", "video",
            "folder", "songs",
            "public_id", publicId
        ));

        String url = (String) uploadResult.get("secure_url");

        Song song = new Song();
        song.setPublicId(publicId);
        song.setLanguage(language);
        song.setFileName(file.getOriginalFilename());
        song.setFilePath(url);
        songRepository.save(song);

        redirectAttributes.addFlashAttribute("success", "Bài hát đã được tải lên thành công!");
        return "redirect:/songs/home";  
    }
    
    @PostMapping("/delete/{id}")
    public String deleteSong (@PathVariable String id, RedirectAttributes redirectAttributes) {
        Song song = songRepository.findById(id).orElse(null);
        
        if (song == null) {
            redirectAttributes.addFlashAttribute("error", "Bài hát không tồn tại.");
            return "redirect:/songs/home";
        }

    	try {
    		String publicId = "songs/" + song.getPublicId();
    		 getCloudinary().uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video" ));
    		 songRepository.delete(song);
    		 redirectAttributes.addFlashAttribute("success", "Xóa thành công");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Xóa không thành công");
			
		}
    	
    	redirectAttributes.addFlashAttribute("success", "Xóa thành công");
    	return "redirect:/songs/home";  
    }
    
    @GetMapping("/")
    public String home(@RequestParam(value = "language", required = false) String language, Model model) {
        List<Song> songs;
        if (language != null && !language.isEmpty()) {
            songs = songRepository.findByLanguage(language);
        } else {
            songs = songRepository.findAll();
        }

        // lấy danh sách tất cả ngôn ngữ duy nhất có trong db
        List<String> languages = List.of("english", "japanese","chinese");
        model.addAttribute("songs", songs);
        model.addAttribute("languages", languages);
        model.addAttribute("selectedLanguage", language);
        return "songs";
    }
    
    
}
