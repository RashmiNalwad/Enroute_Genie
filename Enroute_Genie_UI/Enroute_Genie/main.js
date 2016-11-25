
      // This example requires the Places library. Include the libraries=places
      // parameter when you first load the API. For example:
      // <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

      var source;
      var dest;
      var places = ["Golden Gate Bridge", "Winchester Mystery House", "Half Moon Bay", "Cannery Row", "Bixby Bridge", "Hearst Castle", "Bubblegum Alley","Downtown Ventura",
                "Rodeo Drive",
                "Balboa Park",
                "Santa Barbara",
                "San Luis Obispo",
                "Islay Hill",
                "Bubblegum Alley",
                "McWay Falls",
                "Mission Beach",
                "Carmel-by-the-Sea",
                "Bixby Bridge",
                "Arlington Theatre",
                "Union Square",
                "Malibu",
                "Golden Gate Park",
                "San Simeon",
                "Monterey",
                "San Diego Bay",
                "Lombard Street",
                "Newport Beach",
                "San Fernando Valley",
                "San Diego County",
                "Oxnard",
                "Pismo Beach",
                "Palomar",
                "Ano Nuevo State Park",
                "West Coast",
                "Point Sur State Historic Park",
                "Old Town",
                "Oregon",
                "Sunset Boulevard",
                "Pacific Beach",
                "San Jose",
                "Soda Lake",
                "Rio Theatre",
                "Half Moon Bay",
                "Pfeiffer Beach",
                "Monterey Bay",
                "Silicon Valley",
                "Santa Cruz",
                "Los Angeles",
                "Long Beach Museum of Art",
                "Santa Monica Pier",
                "Yosemite National Park",
                "California",
                "Black Hill",
                "San Diego",
                "Ventura",
                "Huntington Beach",
                "Coronado Bridge",
                "Santa Cruz Beach Boardwalk",
                "Pacific Avenue",
                "Wilder Ranch State Park",
                "Lobero Theatre",
                "Alcatraz Island",
                "Monterey Bay Aquarium",
                "East Beach",
                "Ventura Beach",
                "Long Beach",
                "Laguna Beach",
                "Hollywood",
                "San Francisco",
                "Pacific Coast Highway",
                "San Luis Obispo Botanical Garden",
                "Santa Barbara Botanic Garden"];

      function initMap() {
        var origin_place_id = null;
        var destination_place_id = null;
        var travel_mode = 'DRIVING';
        var map = new google.maps.Map(document.getElementById('map'), {
          mapTypeControl: false,
          center: {lat: -33.8688, lng: 151.2195},
          zoom: 13
        });
        var directionsService = new google.maps.DirectionsService;
        var directionsDisplay = new google.maps.DirectionsRenderer;
        directionsDisplay.setMap(map);

        var origin_input = document.getElementById('origin-input');
        var destination_input = document.getElementById('destination-input');
        var modes = document.getElementById('mode-selector');

        map.controls[google.maps.ControlPosition.TOP_LEFT].push(origin_input);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(destination_input);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(modes);

        var origin_autocomplete = new google.maps.places.Autocomplete(origin_input);
        origin_autocomplete.bindTo('bounds', map);
        var destination_autocomplete =
            new google.maps.places.Autocomplete(destination_input);
        destination_autocomplete.bindTo('bounds', map);

        // Sets a listener on a radio button to change the filter type on Places
        // Autocomplete.
        function setupClickListener(id, mode) {
          var radioButton = document.getElementById(id);
          radioButton.addEventListener('click', function() {
            travel_mode = mode;
          });
        }
       
        function expandViewportToFitPlace(map, place) {
          if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
          } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17);
          }
        }

        origin_autocomplete.addListener('place_changed', function() {
          var place = origin_autocomplete.getPlace();
          source = place.address_components[0].long_name; 
          console.log(source);
          if (!place.geometry) {
            window.alert("Autocomplete's returned place contains no geometry");
            return;
          }
          expandViewportToFitPlace(map, place);

          // If the place has a geometry, store its place ID and route if we have
          // the other place ID
          origin_place_id = place.place_id;
          route(origin_place_id, destination_place_id, travel_mode,
                directionsService, directionsDisplay);
        });

        destination_autocomplete.addListener('place_changed', function() {
          var place = destination_autocomplete.getPlace();
          console.log(place);
          dest = place.address_components[0].long_name;
          if (!place.geometry) {
            window.alert("Autocomplete's returned place contains no geometry");
            return;
          }
          expandViewportToFitPlace(map, place);

          // If the place has a geometry, store its place ID and route if we have
          // the other place ID
          destination_place_id = place.place_id;
          route(origin_place_id, destination_place_id, travel_mode,
                directionsService, directionsDisplay);
        });

        function route(origin_place_id, destination_place_id, travel_mode,
                       directionsService, directionsDisplay) {

          if (!origin_place_id || !destination_place_id) {
            return;
          }
          directionsService.route({
            origin: {'placeId': origin_place_id},
            destination: {'placeId': destination_place_id},
            travelMode: travel_mode
          }, function(response, status) {
            if (status === 'OK') {
              directionsDisplay.setDirections(response);
              var geocoder = new google.maps.Geocoder();
              addMarkers(geocoder,map);
            } else {
              window.alert('Directions request failed due to ' + status);
            }
          });
        }

        var marker;
        function addMarkers(geocoder,map)
        {
           for (var i = 0; i < places.length; i++) {
      		geocoder.geocode( { 'address': places[i] }, function(results, status) {
      			console.log(results + " - " + status);
	      		if (status == 'OK') {
	        		map.setCenter(results[0].geometry.location);
	        		marker = new google.maps.Marker({
	            		map: map,
	            		position: results[0].geometry.location
	        	});

	        	marker.addListener('click', function() {
          			openNav();
        		});

	      		}
    		});	    	
           }
        }

        $(".closebtn").click(function() {
 			 closeNav();
		});

        $(".images").click(function() {
 			 openModal();
 			 currentSlide(1);
		});

		$(".close cursor").click(function(){
			closeModal();
		});

		$(".prev").click(function(){
			plusSlides(-1);
		});

		$(".next").click(function(){
			plusSlides(1);
		});

		$(".demo").click(function(){
			 currentSlide(1);
		});


        /* Set the width of the side navigation to 350px */
		function openNav() {
    		document.getElementById("mySidenav").style.width = "450px";
		}

		/* Set the width of the side navigation to 0 */
		function closeNav() {
    		document.getElementById("mySidenav").style.width = "0";
		}

		function openModal() {
  			document.getElementById('myModal').style.display = "block";
		}

function closeModal() {
  document.getElementById('myModal').style.display = "none";
}

var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("demo");
  var captionText = document.getElementById("caption");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  for (i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";
  dots[slideIndex-1].className += " active";
  captionText.innerHTML = dots[slideIndex-1].alt;
}


      }