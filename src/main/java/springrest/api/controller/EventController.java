package springrest.api.controller;

import java.util.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springrest.api.error.RestException;
import springrest.model.Event;
import springrest.model.User;
import springrest.model.dao.EventDao;
import springrest.model.dao.UserDao;
import springrest.model.service.EventImageService;
import springrest.model.service.NewsImageService;
import springrest.util.Utils;
import javax.persistence.Entity;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class EventController {
	
	@Autowired
    private EventDao eventDao;
	
	@Autowired
    private UserDao userDao;
	
	@Autowired
	private EventImageService eventImageService;

	// Get an event by id
    @RequestMapping(value = "/event/{id}", method = RequestMethod.GET)
    public ResponseEntity<Event> getEvent( @PathVariable Long id,HttpServletRequest request)
    {
    	try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		System.out.println("Fetching Event with id " + id);
        	Event event = eventDao.getEvent(id);
        	if (event == null) {
                System.out.println("Event with id " + id + " not found");
                return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Event>(event, HttpStatus.OK);
    	}  catch (Exception e) {
   		 throw new RestException(400, e.getMessage());
   		}
    	
    }

    // Get all events
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> getEvents(HttpServletRequest request)
    {
    	 try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		List<Event> events = eventDao.getEvents();
     		if(events.isEmpty()){
                return new ResponseEntity<List<Event>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Event>>(events, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    	 
    }
    
    // Get all own events
    @RequestMapping(value = "/ownevents", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> getOwnEvents(HttpServletRequest request)
    {
    	 try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
    		String token = request.getHeader("Authorization");
     		List<Event> events = eventDao.getOwnEvents(Utils.decode(token).getClaim("userId").asLong());
     		if(events.isEmpty()){
                return new ResponseEntity<List<Event>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Event>>(events, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    	 
    }
    
    @RequestMapping(value = "/approvedEvents", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> getApprovedEvents(HttpServletRequest request)
    {
    	 try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular(requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		List<Event> events = eventDao.getApprovedEvents();
     		if(events.isEmpty()){
                return new ResponseEntity<List<Event>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<Event>>(events, HttpStatus.OK);
    	 }  catch (Exception e) {
    		 throw new RestException(400, e.getMessage());
    	 }
    	 
    }
    
    // Get all attendees
    @RequestMapping(value = "/event/{id}/attendees", method = RequestMethod.GET)
    public ResponseEntity<Set<User>> getAttendees(@PathVariable Long id,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		System.out.println("Fetching Event with id " + id);
     		Set<User> attendees = (Set<User>) eventDao.getEvent(id).getAttendees();
     		System.out.println(attendees);
       	 	if(attendees.isEmpty()){
                return new ResponseEntity<Set<User>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<Set<User>>(attendees, HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    	 
    }
    
    // add an attendees
    @RequestMapping(value = "/event/{id}/attendee/{useId}", method = RequestMethod.POST)
    public ResponseEntity<Set<User>> addAttendee(@PathVariable Long id,@PathVariable Long userId,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		//User user = userDao.getUser(userId);
        	Event event = eventDao.getEvent(id);
        	if (event == null)
    			return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
        	event.getAttendees().add(requestUser);
    		eventDao.saveEvent(event);
    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
    
//    @RequestMapping(value = "/event/{id}/attendee", method = RequestMethod.POST)
//    public ResponseEntity<Set<User>> enrollEvent(@PathVariable Long id,HttpServletRequest request)
//    {
//    	try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdminOrRegular (requestUser))
//     			throw new RestException(400, "Invalid Authorization");
//     		//User user = userDao.getUser(userId);
//        	Event event = eventDao.getEvent(id);
//        	if (event == null)
//    			return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
//        	event.getAttendees().add(requestUser);
//    		eventDao.saveEvent(event);
//    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
//    	}  catch (Exception e) {
//   		 	throw new RestException(400, e.getMessage());
//   		}
//    }
    
    @RequestMapping(value = "/event/{id}/attendee/username", method = RequestMethod.POST)
    public ResponseEntity<Set<User>> addAttendeeByUsername(@PathVariable Long id,@RequestBody JSONObject json_object,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		System.out.println((String)json_object.get("username"));
     		User user = userDao.getUserByUsername((String)json_object.get("username"));
        	Event event = eventDao.getEvent(id);
        	if (event == null)
    			return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
        	event.getAttendees().add(user);
    		eventDao.saveEvent(event);
    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
    
    @RequestMapping(value = "/event/{id}/addAttendee/{userId}", method = RequestMethod.POST)
    public ResponseEntity<Set<User>> addAttendeeByUserId(@PathVariable Long id,@PathVariable Long userId,HttpServletRequest request)
    {
    	System.out.println("wtfwtfwtf");
    	try {
//    		String token = request.getHeader("Authorization");
//     		Utils.decode(token).getClaim("userId").asLong();
//     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
//     		if (!Utils.proceedOnlyIfAdmin (requestUser))
//     			throw new RestException(400, "Invalid Authorization");
     		User user = userDao.getUser(userId);
        	Event event = eventDao.getEvent(id);
        	if (event == null)
    			return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
        	event.getAttendees().add(user);
    		eventDao.saveEvent(event);
    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
    
    // drop an attendees
    @RequestMapping(value = "/event/{id}/attendee/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Set<User>> dropAttendee(@PathVariable Long id,@PathVariable Long userId,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		User user = userDao.getUser(userId);
        	Event event = eventDao.getEvent(id);
        	if (event == null)
        		throw new RestException(400, "Event not found");
        	event.getAttendees().remove(user);
    		eventDao.saveEvent(event);
    		return new ResponseEntity<Set<User>>(event.getAttendees(), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
    
    // create a new event
    @RequestMapping(value = "/events", method = RequestMethod.POST)
	public ResponseEntity<Event> createEvent(@RequestParam(value = "image",required=false) MultipartFile image,@RequestParam("name") String name,@RequestParam("location") String location,@RequestParam("description") String description,@RequestParam("eventDate") String eventDate,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("status") String status,HttpServletRequest request) {
    	Event event = new Event();
    	System.out.println(name);
    	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdminOrRegular (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		if (name == null || startTime == null || endTime == null || location == null
     				||eventDate == null||description == null)
        		throw new RestException( 400, "missing required field(s)." );
     		event.setName(name);
     		event.setDescription(description);
     		event.setLocation(location);
     		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     		event.setEventDate(sdf.parse(eventDate));
     		sdf = new SimpleDateFormat("HH:mm");
     		event.setStartTime(new Time(sdf.parse(startTime).getTime()));
     		event.setEndTime(new Time(sdf.parse(endTime).getTime()));
     		event.setOrganizer(requestUser);
     		if (Utils.orgnaziedByEventOrganizor(requestUser))
     			event.setStatus(1);
     		event.setStatus(Integer.parseInt(status));
     		if (image==null||image.isEmpty()) {
     			event.setImageUrl("assets/images/course/cu-4.jpg");
     		} else {
     			event = eventDao.saveEvent(event);
     			String fileName = image.getOriginalFilename();
          		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
          		event.setImageUrl("http://localhost:8080/springrest/api/event-image/event"+event.getId()+"."+fileType);
            	this.eventImageService.store(image, "event"+event.getId());
     		}
     		return new ResponseEntity<Event>(eventDao.saveEvent(event),HttpStatus.CREATED);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
    
    // edit a event
   	@RequestMapping(value = "/event/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody JSONObject json_object,HttpServletRequest request) {
   		System.out.println("Updating Event " + id);
   		try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Event event = eventDao.getEvent(id);
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
    		event.setName((String)json_object.get("name"));
     		event.setDescription((String)json_object.get("description"));
     		event.setLocation((String)json_object.get("location"));
     		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     		event.setEventDate(sdf.parse((String) json_object.get("eventDate")));
     		sdf = new SimpleDateFormat("HH:mm");
     		event.setStartTime(new Time(sdf.parse((String)json_object.get("startTime")).getTime()));
     		event.setEndTime(new Time(sdf.parse((String)json_object.get("endTime")).getTime()));
     		event.setOrganizer(requestUser);
     		if(((String)json_object.get("status")).equals("approved")) {
     			System.out.println((String)json_object.get("status"));
     			event.setStatus(1);
     		}
     		if(((String)json_object.get("status")).equals("rejected")) {
     			System.out.println((String)json_object.get("status"));
     			event.setStatus(2);
     		}
     		if(((String)json_object.get("status")).equals("submitted")) {
     			System.out.println((String)json_object.get("status"));
     			event.setStatus(0);
     		}
   			return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a event
 	@RequestMapping(value = "/event/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<Event> deleteEvent(@PathVariable("id") long id,HttpServletRequest request) {
        System.out.println("Fetching & Deleting Event with id " + id);
        try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
     		Event event = eventDao.getEvent(id);
            if (event == null) {
                System.out.println("Unable to delete. Event with id " + id + " not found");
                return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
            }
            eventDao.deleteEvent(event);
        	return new ResponseEntity<Event>(HttpStatus.NO_CONTENT);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
    }
 	
 	// approve a event
	@RequestMapping(value = "/event/approve/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<Event> approveEvent(@PathVariable Long id,HttpServletRequest request) {
		try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Event event = eventDao.getEvent(id);
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
    		event.setStatus(1);
       		return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
   	}
	
	// reject a event
	@RequestMapping(value = "/event/reject/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Event> rejectEvent(@PathVariable Long id,HttpServletRequest request) {
	   	System.out.println("Rejecting Event " + id);
	   	try {
    		String token = request.getHeader("Authorization");
     		Utils.decode(token).getClaim("userId").asLong();
     		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
     		if (!Utils.proceedOnlyIfAdmin (requestUser))
     			throw new RestException(400, "Invalid Authorization");
    	   	Event event = eventDao.getEvent(id);
    		event.setStatus(2);
    		System.out.println(event.getId());
    		if (event == null)
    			return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
       		return new ResponseEntity<Event>(eventDao.saveEvent(event), HttpStatus.OK);
    	}  catch (Exception e) {
   		 	throw new RestException(400, e.getMessage());
   		}
	}
	
	@RequestMapping(value = "/event-image/{imageName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String imageName) {
      Resource file = this.eventImageService.loadFile(imageName);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .body(file);
    }
}