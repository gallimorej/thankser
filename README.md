# Thankser

Thankser is a slackbot that says "thank you" in various languages. When just saying a simple "thanks" isn't enough, add some novelty by using a different tongue.

## Features

* Returns the translation of "thank you" in a specified language.
* Returns one of a number of responses when asked for a snarky translation.
* Provides a listing of the languages it knows.
* Logs requested languages that it doesn't know.
* Provides usage.

## Installation

### Shared Thankser

The easiest way to get started with Thankser in your Slack workspace is to use the shared version running at https://thankser.herokuapp.com. To do this, you need to create and configure a new slash command in your Slack workspace.

1. Create the `/ty` slash command in your Slack workspace. Follow the [directions from Slack](https://api.slack.com/slash-commands) for doing this.
2. Configure the `/ty` slash command. Here are the configuration settings:

Setting | Value
---|---
Command | `/ty`
Request URL | `https://thankser.herokuapp.com/say-thanks`
Short Description | `Says "thank you" in different languages.`
Usage Hint | `[language] [optional: any text following the thanks, including @mentions]`

Also, check the box for the __Escape channels, users, and links sent to your app__ setting.

### Your own Thankser
Setting up your own Thankser configuration is a little more involved.

1. Set up a MongoDB instance. (I use a hosted MongoDB instance from [mLab](https://mlab.com/).) 
2. Deploy Thankser somewhere. (As you can tell, the shared Thanker setup is in [Heroku](https://www.heroku.com/).)
3. Create the `/ty` slash command in your workspace like you would for the [Shared Thankser](#shared-thankser) setup.

If you need more information to get your own Thankser configuration up and running, reach out and I'll try to help.

## Usage

You can call Thankser from Slack, a browser, or the command line.

### From Slack

After you install Thankser in your Slack workspace, you call Thankser by invoking the following slash command:
 
    /ty

To find out how to say "thank you" in a particular language, you need to identify the language using either the name of the language (like `german`) or the two-letter [ISO 639](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) language code (like `de`).

For example:

    /ty german
    /ty de

You can also find out which languages Thankser knows by typing the following command:

    /ty ?

Finally, if you want to thank someone directly, you can @mention them following the language you want. You can @mention as many people as you'd like to thank.

For example:

    /ty german @SlackUser

### From a browser

To call Thankser from a browser, access the following URLs:

    https://thankser.herokuapp.com/say-thanks?text=
    https://thankser.herokuapp.com/say-thanks?text=?
    https://thankser.herokuapp.com/say-thanks?text=german

To see which unknown languages users have requested, access the following URL:

    https://thankser.herokuapp.com/show-unknown-languages

### From a command line

To call Thankser from the command line:

    $ curl -d "text=german" -H "Content-Type: application/json" -X POST https://thankser.herokuapp.com/say-thanks
    $ curl https://thankser.herokuapp.com/show-unknown-languages
    
## Adding new languages

To add new languages to Thankser, modify the [`thankses.json`](https://github.com/gallimorej/thankser/blob/master/data/thankses.json) file and redeploy Thankser. 

## Origins of Thankser

###The idea
The idea for Thankser originated from some feedback I received from a colleague. She told me she and others really appreciated it when I said "thank you" after they had done something to support me. She said I was "pretty good" about doing that. Maybe even better than most. But "pretty good" isn't good enough. I needed to be better at the basics. Building Thankser was a way for me to internalize that feedback. It's hard for me to forget to say thank you now when I've spent so much time building an app that does just that.

###Clojure
I wanted to make a run at learning to code again. [I asked the Twitterverse which language I should start with.](https://twitter.com/jgallimore/status/1051264810321633280) I got lots of input, but what cinched it for me was [Gene Kim's](https://twitter.com/RealGeneKim) response. First, he told me Clojure had "changed his life" and helped him rediscover the joy of programming. He also said he would be willing to pair with me. How could I pass that up a life-changing experience and learning from Gene? 

The fact that I could even build Thankser at all is a credit to Gene's efforts in teaching me. In addition to learning the Clojure language, it's been a long time since I've coded anything so I had a lot to learn about lots of stuff to even get "hello, world" working. But I had a lot of fun learning it all. Thanks to Gene for sharing his time, talents, and knowledge to make me productive in an IDE again. 

## License

Copyright © 2019 Jeff Gallimore

Distributed under the [Eclipse Public License version 1.0](https://www.eclipse.org/legal/epl-v10.html).
