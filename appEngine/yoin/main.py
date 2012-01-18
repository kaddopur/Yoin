#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from google.appengine.api import urlfetch
from google.appengine.ext.webapp import template
import datetime
import re
import os
import simplejson
from dbmodel import *
import sys
import urllib2

class MainHandler(webapp.RequestHandler):
    def get(self):

        template_values = {
            'videos': Video.all().order('-date').fetch(10),
            'status': self.request.get('status')
            }
        path = os.path.join(os.path.dirname(__file__), 'index.html')
        self.response.out.write(template.render(path, template_values))


class InsertUrl(webapp.RequestHandler):
    def get(self):
         
        video_url = self.request.get('video_url')
        video_tag = self.request.get('video_tag')
        refresh = self.request.get('refresh')
        
        #refresh
        if refresh == 'true':
            self.refresh()
            self.redirect('/')
            return
        
        # find video_key
        try:
            target = video_url.index('watch?v=') + len('watch?v=')
        except ValueError:
            self.redirect('/?status=error')
            return

        video_key = video_url[target: target+11]
        self.response.out.write(video_key)
        
        target_video = Video.all().filter('vid =', video_key).get()        
        if not target_video:
            new_video = Video(vid=video_key)
            new_video.title, new_video.uploader, new_video.view, new_video.since, status = self.fetch_data('http://www.youtube.com/watch?v=' + video_key)

            new_video.good = 0
            new_video.bad = 0
            new_video.tag = video_tag
            new_video.put()
            
            self.send_notification(new_video.title)
            self.redirect('/?status=ok')
        else:
            self.redirect('/?status=exist')
            
            
    def send_notification(self, video_title):
        json_data = {}
        json_data['key'] = 'SPf3JHVLyR4rxOvJCcBQ48Gx52ZaLaMt9qPDGLN0'
        json_data['channel'] = ''
        json_data['type'] = 'android'
        json_data['data'] = {'title': 'There is a new clip', 'alert': video_title}
        json_string = simplejson.dumps(json_data)
        
        req = urllib2.Request(url='https://api.parse.com/1/push',
                              data=json_string)
        req.add_header('Content-Type', 'application/json')
        r = urllib2.urlopen(req)
        

    def refresh(self):
        videos = Video.all().fetch(1000)
        for v in videos:
            v.title, v.upoader, v.view, v.since, status = self.fetch_data('http://www.youtube.com/watch?v=' + v.vid)
            if status == 200:
                v.put()
            else:
                v.delete()
    
    def fetch_data(self, url):
        result = urlfetch.fetch(url)
        if result.status_code == 200:
            # find title
            target = result.content.index('id="eow-title"') + len('id="eow-title"')
            target = result.content.index('title="', target) + len('title="')
            title = result.content[target: result.content.index('">', target)]

            # find uploader
            target = result.content.index('<a href="/user/') + len('<a href="/user/')
            uploader = result.content[target: result.content.index('"', target)]

            # find view
            target = result.content.index('class="watch-view-count"') + len('class="watch-view-count"')
            target = result.content.index('<strong>', target) + len('<strong>')
            view = int(result.content[target: result.content.index('</strong>', target)].replace(',', ''))    

            # find since
            month = {'Jan': 1,
                      'Feb': 2,
                     'Mar': 3,
                     'Apr': 4,
                     'May': 5,
                     'Jun': 6,
                     'Jul': 7,
                     'Aug': 8,
                     'Sep': 9,
                     'Oct': 10,
                     'Nov': 11,
                     'Dec': 12}
            target = result.content.index('id="eow-date"') + len('id="eow-date"')
            target = result.content.index('>', target) + len('>')
            #for taiwan
            since_str = result.content[target: result.content.index('</span>', target)]
            try:
                since_str.index('-')
                since_str = since_str.split('-')
                since =  datetime.date(*[int(s) for s in since_str])
            except ValueError:
                since_str = since_str.split(' ')
                since =  datetime.date(int(since_str[2]), month[since_str[0]], int(since_str[1][:-1]))
    
            return title, uploader, view, since, result.status_code
            

class GetVideos(webapp.RequestHandler):
    def get(self):
        videos = Video.all().fetch(10)
        order = self.request.get('order')
        offset = self.request.get('offset')
        amount = self.request.get('amount')

        if not order:
            order = 'since'
        if not offset:
            offset = 0
        if not amount:
            amount = 20
        
        videos = Video.all().order('-'+order).fetch(int(amount), int(offset))


        video_out = [{'vid': v.vid, 
                      'title': v.title,
                      'uploader': v.uploader,
                      'view': v.view,
                      'since': str(v.since),
                      'good': v.good,
                      'bad': v.bad} for v in videos]
        self.response.headers['Content-Type'] = "text"
        self.response.out.write(simplejson.dumps(video_out))


def main():
    code = sys.getdefaultencoding()
    if code != 'utf8':
        reload(sys)
        sys.setdefaultencoding('utf8')
        
    application = webapp.WSGIApplication([('/', MainHandler),
                                          ('/insert_url', InsertUrl),
                                          ('/get_videos', GetVideos)],
                                         debug=True)

    #/refresh videos

    util.run_wsgi_app(application)


if __name__ == '__main__':
    main()
