# Kenneth Salanga Eulerity Hackathon Submission #

## Chronological implementation of how I got functionalities to work: ##

* JSoup Scraping for images on a single web page
	* Implemented the JSoup library to find all image tags given the request's input address.

* URL parsing
	* I decided URL parsing was a problem that could have arisen if a client doesn't type the full address.
		* That's something you have to deal with in software though, data won't always be formatted to the programmer's liking
		* so I made that a priority in this challenge.
	* There were two URL parsing issues that I noticed:
		1. The url of an image tag when scraping
		2. The url that the client types when they submit
	* Image src urls
		* Some image url tags work only relative to the website. Makes sense, if the server has
		* access to that resource in their domain, sending that relative url is much easier and nicer compared to inputting the full address.
		* Also, what if their domain name changes? Then relative paths would still work for them.
		* As a simple fix, I just ran through all of the ImageURLs that I found with JSoup,
		* checked to see if the src started with a / followed by characters,
		* and I deemed that as a relative path.
		* Given this relative path, I then added it onto the url
		* Cool, we now have clickable images with a relative path!
	* Client url isn't properly formatted
		* If a client types in google.com,
		* JSoup doesn't understand that as a valid url. It needs the http scheme and optionally www.
		* I can't just return an error for this though, so I added the HTTP schemes and www. to the url.
		* I did this under a format function.
		* Now, if a client enters an empty string, I have that covered.
		* Theoretically an empty string with my format function will now make the url http://.
		* This is still an invalid URL so I needed one more validation check.
		* I added an extra commons library to check if this formatted url was valid or not.
		* After this final validation, we know it's either a valid or invalid URL.
	* Abstracted these functionalities into a separate util Folder inside an ImageUrlParser class
		* The functions included: 
			*find all image sources with JSoup given a url
			* parsing both url issues
			* checking validity of a url.

* Implementing Crawling with an external Maven Repository
	* Used Crawler4j as it had many benefits of multithreading and politeness that Eulerity's hackathon was looking for.
	* Adding this library to my Maven repository was a big challenge and unexpectedly took up a lot of the time for the hackathon.
	* It was a fun challenge for me though because I got to understand a lot more about how dependencies worked.
	* Debugging Crawler4j Library Issue and Fix
		* First, I noticed that when I installed Crawler4j,
		* it was telling me I was unauthorized from accessing other dependencies that it was using like sleepycat.
		* I learned recently how static and dynamic dependencies/ libraries worked in C in my Systems Programming Course
		* so I thought maybe I just had to manually add sleepycat as a dependency.
		* The same issue was happening with 401 unauthorized.
		* So I asked maybe I got the version number wrong? I did. Crawler4j was using 5.0.84 which was in the Spring Repositories and not
		* the central Maven repositories. Alright, so I added all of the Spring Plugins and made sure sleepycat was the correct version. Same error.
		* This was very frustrating, but I then realized that a 401 unauthorized means that the files aren't accessible through the Network,
		* not via the physical files the pom.xml was trying to sift through.
		* So the entire solution was just Maven blocking external HTTP repositories.
		* Crawler4j used sleepycat which was a database repository so it only makes sense that this repository would be making HTTP requests.
		* The fix was to comment out the http blocker in the settings.xml.
		* After hours of debugging and a sigh of relief, Crawler4j was fully operational.
	* Abstracted Modules into Separate Crawler Folder
		* Crawler Class represents a single thread.
		* Crawler Controller Class represents the configurations and the starting url of the crawl.
		* The controller is what gets referenced and starts the call in the POST servlet function of ImageFinder.
	* Implementing Multi Page crawling given a starting domain
		* The Crawler Controller was responsible for hooking up most of the configurations for the Web crawl to begin.
		* With Crawler4j, I put in the controller the necessary amount of:
			* threads (Crawler classes) I wanted to run
			* politeness
			* depth
			* the default values I decided were politeness 50 ms (not so polite :( but can configure), 100 threads, and 2 depth.
			* the 100 threads could have caused overhead, but compared to the number of links the Web Crawl will visit, the threads were miniscule.
	* Implementing Multithreading
		* The Controller instructs the Crawlers to start running given a starting URL, but the Crawler Class did the bulk of the work.
		* The Crawler class visits a page, and I reference the ImageURLParser method from before to look for all Images given that page.
		* Each time a page was visited, the page was added to a queue that threads could go and grab to find images for.
		* Put all of those collected images per thread (per page) into a single thread safe array pool.
		* Thread safe array aggregates all of the images once the crawling is done and it returns that array as a response to the user.
	* Implementing Politeness
		
* Things I would have added given more time
	* The politeness, amount of threads to crawl, and the depth of the crawl were hard coded.
	* I wanted to set up a client being able to tweak these settings and access it through the request's body.
		* Also, the max number of pages could have been configured as well, but I didn't add that configuration in the controller.
	* Setting up the web crawler and all of the parsing took up the full 48 hours ( technically not 48 hours since sleep and other activities were in between haha ;) ).
	* Multithreading works great for multiple depths and multiple pages of a domain,
	* but for just a single page lookup, A single Jsoup query and return was much faster compared to the Crawler4j implementation with just a single page.
		* I was trying to understand why this was, and I came to a few conclusions such as the buildup for Crawler4j was heavy.
		* It accesses databases via network requests and sets up many configurations which definitely slows down time compared to the light Jsoup.
		* Maybe in tandem with the flexible depth a user requests,, if the user requests a depth of just the first page, 
		* I would have just made it use Jsoup rather than the bulky Crawler4j and for anything else, I'll let the multithreads handle the big load.
	* Logo implementation
		* I was really excited thinking of this problem but unfortunately the multithreading and multipage + parsers took up the time.
		* I would have compared the src descriptions to the website though, 
		* and if there was a match, that image could have a high chance of being a logo.
	* Try out JUnit Testing more