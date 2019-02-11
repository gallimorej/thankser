#
# entry points
#

autotest:
	lein test-refresh :growl

slack:
	lein run

ring:
	#lein ring server-headless
	lein ring server

# run from Heroku

web:
	java $(JVM_OPTS) -cp target/thankser.jar clojure.main -m thankser.web


#
# utilities
#

uberjar:
	lein uberjar

push-heroku:
	# usually you'll want to use this...
	#git push heroku master
	#
	# but when you need to push from non-master branch, use this...
	#
	git push heroku add-worker:master

open-heroku:
	heroku open

config-heroku:
	heroku addons:create scheduler:standard

worker-heroku:
	heroku run worker
