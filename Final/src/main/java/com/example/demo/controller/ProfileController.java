package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Profile;
import com.example.demo.repository.ProfileService;

@RestController
@RequestMapping
public class ProfileController {
	@Autowired
	private ProfileService profileService;
	
	@PostMapping("/create")
	public ResponseEntity createProfile(@RequestBody Profile profile) throws Exception {
		System.out.println(profile.getFirstName()+ " "+profile.getHobby().get(0).getHobbyName());
		try {
			return new ResponseEntity(profileService.createProfileDocument(profile),HttpStatus.CREATED);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}
//	 @GetMapping("/{id}")
//	    public Profile findById(@PathVariable String id) throws Exception {
//	        return profileService.findById(id);
//	    }
	 @GetMapping("/search")
	 public List<Profile> search(@RequestParam("hobby") String hobby ) throws Exception{
		return profileService.searchByHobby(hobby);
		 
	 }
	 @GetMapping("/profile/search-name")
	 public List<Profile> searchByName(@RequestParam("name") String name) throws Exception{
		 return profileService.findByName(name);
	 }
	 @GetMapping("/profile")
	 public List<Profile> searchAll() throws Exception{
		 return profileService.findAll();
	 }
}
