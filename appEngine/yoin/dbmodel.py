from google.appengine.ext import db

class Video(db.Model):
	vid = db.StringProperty(required=True)
	
	title = db.StringProperty()
	uploader = db.StringProperty()
	view = db.IntegerProperty()
	since = db.DateProperty()
	good = db.IntegerProperty()
	bad = db.IntegerProperty()
	
	date = db.DateTimeProperty(auto_now_add=True)
    
