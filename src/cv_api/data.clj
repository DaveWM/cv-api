(ns cv-api.data
  (:require [clj-time.core :refer [date-time]]
            [mikera.image.core :as imagez]
            [clojure.java.io :refer [as-url input-stream as-file]]
            [amazonica.aws.s3 :as s3]
            [clojure.string :as s]
            [cheshire.core :refer [parse-string]]
            [environ.core :refer [env]])
  (:import [javax.imageio ImageIO])
  (:import [java.io]))

(def base-url "https://cv-api.herokuapp.com/")
(defmacro resource [path]
  (str base-url path))

(def raw-data
  {:personal {:email "dwmartin41@gmail.com"
              :phone "07588361916"
              :github-user "DaveWM"
              :linkedin-url "https://www.linkedin.com/in/davewm"
              :codewars-user "DaveWM"}
   :summary "I am an experienced full-stack Clojure/ClojureScript dev, currently living in Liverpool. I have been a developer for 8 years, in which time I’ve worked across a variety of industries, languages, tech stacks, and job roles. I have designed, architected, and built web apps; worked on desktop apps with thousands of users; and created event-driven Kafka Streams applications.

In my free time, I maintain a few (small) open source projects, such as:
* [reagent-material-ui](https://github.com/DaveWM/reagent-material-ui) - a [Reagent](https://reagent-project.github.io/) wrapper for [Material UI](https://reagent-project.github.io/)
* [Larch](https://github.com/DaveWM/larch) - a very minimal CLJS \"framework\", loosely following the Elm architecture
* [ngWizard](https://github.com/DaveWM/ngWizard) - a wizard component for AngularJS
* [ATTyC](https://github.com/DaveWM/attyc) - type checking for AngularJS templates, using TypeScript

I have also written a few open source web apps, such as:
* [Lobster Writer](https://lobster-writer.co.uk/) - an app to help you write quality essays
* [Phonsole](https://quality-controller-hamster-75314.netlify.com/) - view terminal output on your phone/tablet

I write the [Clojure Conundrums](https://clojure-conundrums.co.uk/) blog."
   :education [{:from (date-time 2008 9 1)
                :to (date-time 2011 7 1)
                :name "University of Liverpool"
                :highlights ["BSc in Physics, 1st Class Honours."
                             "81% average in exams, lab work and coursework."
                             "3rd year project involves data mining/signal analysis using C++ and linux."
                             "Awarded Physics Department Attainment Scholarship, and Wynn Evans Memorial Prize (awarded to top sudent in BSc physics program)"]}
               {:from (date-time 2001 9 1)
                :to (date-time 2008 6 1)
                :name "Calday Grange Grammar School"
                :highlights ["3 As at A level (Maths, Physics, and Chemistry)"
                             "Awarded Wynn Williams Memorial Prize for Astrophysics"
                             "11 GCSEs"]}]
   :hobbies [{:title "Motorsport"
              :description "I have raced in the BRSCC Mazda MX5 and Track Attack Toyota MR2 series"
              :img (resource "resources/public/racing.jpg")}
             {:title "Reading"
              :description "I'm a big fan of sci-fi books"
              :img (resource "resources/public/hyperion.jpg")}
             {:title "Snowboarding"
              :description "I enjoy snowboarding during the winter"
              :img (resource "resources/public/snowboarding.jpg")}]
   :work-history [{:title "Funding Circle"
                   :from (date-time 2018 1 8)
                   :url "https://www.fundingcircle.com/uk/"
                   :bullet-points ["Building event-driven Kafka Streams microservices in Clojure"]}
                   {:title "UVD"
                   :img (resource "resources/public/UVD.png")
                   :from (date-time 2016 9 1)
                   :to (date-time 2017 12 1)
                   :url "https://uvd.co.uk"
                   :bullet-points ["Worked on the [Limpid Markets](https://www.limpidmarkets.co.uk/) trading platform, which uses AngularJS, React and Redux. As well as writing new features and fixing bugs, I also provided direction on the overall architecture of the app."
                                   "Re-wrote the build system for Limpid Markets - see [this blog post](https://www.uvd.co.uk/blog/how-we-simplified-our-frontend-build/)"
                                   "Wrote the next version of the flagship product of an IOT street lights company. The app is written in TypeScript and Angular, and uses RxJS and Ramda"
                                   "Worked on a photo sharing app, written in Elm"]}
                  {:title "Potato"
                   :img (resource "resources/public/P.png")
                   :from (date-time 2015 11 1)
                   :to (date-time 2016 9 1)
                   :url "https://p.ota.to/"
                   :bullet-points ["Developing web apps and marketing sites for clients such as Google and Nest."
                                   "Deployed apps to google app engine."
                                   "Working on an internal web app, using ES6, react and redux. I added tests using mocha, chai and sinon."]}
                  {:title "Sporting Solutions"
                   :img (resource "resources/public/SS.png")
                   :from (date-time 2015 4 1)
                   :to (date-time 2015 11 1)
                   :url "http://www.sportingsolutions.com"
                   :bullet-points ["Working in a team responsible for receiving data from external feed providers, and passing it on to other teams via a message queue."
                                   "The system was mainly composed of 2 large C# console apps, which communicated using rabbitMQ. There were also a number of smaller console apps, an internal MVC website used to manage the feed data, and an asp.net REST api for use by other teams."
                                   "Used a variety of technologies to consume external feeds, such as TCP sockets, HTTP polling and IBM WebSphere"
                                   "I was responsible maintaining an internal MVC website. I was also in charge of migrating this website to an angular SPA, which I built using ES6 + babel, angular 1.4, angular material, LESS and gulp. Currently in the prototype phase."
                                   "I did the vast majority of the work to migrate the logging in one application from an old, unmaintained framework to Graylog."
                                   "I was put in charge of re-writing a suite of acceptance tests written in specflow. I also set up a CI project to run these tests."
                                   "I was involved with improving our teamcity build projects, with the aim of making them more consistent, and also faster."
                                   "Was tasked with investigating docker and container management frameworks, with the aim of breaking our existing services down into microservices"
                                   "Created the [ObjectDiffer](https://github.com/sportingsolutions/ObjectDiffer) and [GraylogApiHelpers](https://github.com/sportingsolutions/SS.GraylogApiHelpers) open source projects."
                                   ]}
                  {:title "Blinkbox"
                   :img (resource "resources/public/BB.png")
                   :from (date-time 2014 5 1)
                   :to (date-time 2015 4 1)
                   :url "http://www.blinkbox.com/"
                   :bullet-points ["Working in the \"admin\" department, creating web apps for internal use - e.g. asset management, video file ingestion, user management, etc."
                                   "Mainly working on an angular SPA web app, with an ASP.NET Web Api 2 backend."
                                   "Worked with build tools such as grunt, bower and npm, as well as testing frameworks such as karma and protractor"
                                   "My team (for which I was lead developer) came second in the company hackathon."]}
                  {:title "Globecast"
                   :img (resource "resources/public/GC.jpg")
                   :from (date-time 2013 11 1)
                   :to (date-time 2014 4 1)
                   :url "http://www.globecast.com/"
                   :bullet-points ["Most of my time was spent working on MVC 4/5 projects (both new and existing)"
                                   "Designed and built new web apps (using MVC/AngularJS) to perform tasks such as comparing programme schedules, and performing QC on video files."
                                   "Worked on an existing system for ingesting video files, processing them, and sending them to a 3rd party scheduling program. This system was composed of multiple C# console apps."
                                   "Was put in charge of the purchase order system (written in MVC 3), which was used for assigning budgets, producing financial reports, and sending purchase orders. I was completely responsible for a complete overhaul of the budgeting system, which involved lots of back end work, writing sql to migrate the existing data, and using angularjs and D3 to create a page for editing/visualising the budget."
                                   "Working with technologies such as Kendo UI, Angular JS, LESS, and D3 on the client side, and SignalR, PostSharp and Entity Framework on the server side."
                                   "Performed several video file migrations from one storage system to another - several hundred terabytes of data in total."
                                   ]}
                  {:title "CPL Software"
                   :img (resource "resources/public/CPL.jpg")
                   :from (date-time 2011 12 1)
                   :to (date-time 2013 11 1)
                   :url "http://www.cplsoftware.com/"
                   :bullet-points ["Mainly working on a C# winforms app for property factors, called RPM. This app had a variety of functions, including: producing invoices, calculating taxes, sending out letters/emails, and importing bank transactions."
                                   "1st, 2nd and 3rd line support"
                                   "Writing SQL stored procedures, performing data migrations/fixes, and general database maintenance"
                                   "Frequent use of Entity Framework and LINQ, using LINQKit"
                                   "Maintaining and updating a web API written for WCF - this allowed consumers to perform the core functions of the winforms app."
                                   "Worked on a web portal written in MVC 2 (involved use of jquery, ajax and css)"
                                   "Worked on a prototype web app in MVC 4 (using technologies such as twitter bootstrap, angular js, and DevExpress MVC controls)"]}
                  {:title "Redwood Technologies"
                   :img (resource "resources/public/RW.png")
                   :from (date-time 2011 8 1)
                   :to (date-time 2011 11 1)
                   :url "http://www.RedwoodTech.com"
                   :bullet-points ["First and second line support"
                                   "Worked on the internal ticketing system (written in PHP/MySQL)"]}]
   :technologies [{:name "C#" :experience 4 :type :language :img "https://raw.githubusercontent.com/sschmid/Entitas-CSharp/develop/Readme/Images/csharp.png"}
                  {:name "SQL" :experience 3.5 :type :language :img "https://www.vectorlogo.zone/logos/expressjs/expressjs-card.png"}
                  {:name "Javascript" :experience 4 :type :language :img "https://www.codementor.io/assets/page_img/learn-javascript.png"}
                  {:name "HTML 5" :experience 4 :type :language :img "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/HTML5_logo_and_wordmark.svg/1000px-HTML5_logo_and_wordmark.svg.png"}
                  {:name "CSS3" :experience 4 :type :language :img "http://connexo.de/img/logos/CSS3_Logo.png"}
                  {:name "SCSS" :experience 1.8 :type :language :img "http://codezyn.com/wassup/wp-content/uploads/2014/10/317889.png"}
                  {:name "LESS" :experience 2.5 :type :language :img "http://lesscss.org/public/img/logo.png"}
                  {:name "Clojure" :experience 2.5 :type :language :img "https://pupeno.files.wordpress.com/2015/08/clojure-logo.png"}
                  {:name "ClojureScript" :experience 2 :type :language :img "https://avatars2.githubusercontent.com/u/12118456?v=3&s=200"}
                  {:name "TypeScript" :experience 1.25 :type :language :img "https://raw.githubusercontent.com/remojansen/logo.ts/master/ts.png"}
                  {:name "Python" :experience 0.8 :type :language :img "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Python-logo-notext.svg/1024px-Python-logo-notext.svg.png"}
                  {:name "Hy" :experience 0.1 :type :language :img "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Hy_Header_Logo.svg/800px-Hy_Header_Logo.svg.png"}
                  {:name "Haskell" :experience 0.25 :type :language :img "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Haskell-Logo.svg/1200px-Haskell-Logo.svg.png"}
                  {:name "Elm" :experience 0.25 :type :language :img "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Elm_logo.svg/512px-Elm_logo.svg.png"}
                  {:name "Elixir" :experience 0.1 :type :language :img "https://avatars2.githubusercontent.com/u/1481354?v=3&s=400"}

                  {:name "AngularJS" :experience 2.5 :type :FE :img "https://avatars0.githubusercontent.com/u/139426?v=3&s=400"}
                  {:name "Angular" :experience 1 :type :FE :img "http://2.bp.blogspot.com/-PPfS5cNbvGQ/VjnIYSaAB2I/AAAAAAAABHo/RunP4lKpdCM/s1600/Angular-2.png"}
                  {:name "Material Design" :experience 2 :type :FE :img "http://i.imgur.com/AMf9X7E.jpg"}
                  {:name "Reagent" :experience 1.5 :type :FE :img "https://avatars1.githubusercontent.com/u/9254615?v=3&s=400"}
                  {:name "Re-Frame" :experience 1 :type :FE :img "https://raw.githubusercontent.com/Day8/re-frame/master/images/logo/re-frame_256w.png"}
                  {:name "Twitter Bootstrap" :experience 2.5 :type :FE :img "https://pbs.twimg.com/profile_images/563544962109693952/C9QJ7V3X.png"}
                  {:name "jQuery" :experience 1.5 :type :FE :img "http://precision-software.com/wp-content/uploads/2014/04/jQurery.gif"}
                  {:name "React" :experience 1 :type :FE :img "http://www.jsweet.org/wp-content/uploads/2016/04/react-logo.png"}
                  {:name "Redux" :experience 0.8 :type :FE :img "https://react-redux.herokuapp.com/logo.jpg"}
                  {:name "D3" :experience 0.7 :type :FE :img "https://raw.githubusercontent.com/d3/d3-logo/master/d3.png"}
                  {:name "RxJS" :experience 1.2 :type :FE :img "https://avatars3.githubusercontent.com/u/984368?v=3&s=400"}
                  {:name "Backbone" :experience 0.5 :type :FE :img "http://2.bp.blogspot.com/-xdZxDAStMTc/UQRY8nCnmuI/AAAAAAAAW98/sqp4pXNrlE8/s1600/Backbone_logo_logo_only.png"}
                  {:name "Material UI" :experience 0.5 :type :FE :img "http://cdn.designbeep.com/wp-content/uploads/2014/11/material-ui.jpg"}
                  {:name "Materialize" :experience 0.3 :type :FE :img "http://www.nerdystickers.com/thumbnail.asp?file=assets/images/materializecss-001.png&maxx=300&maxy=0"}
                  {:name "NgRx" :experience 0.4 :type :FE :img "http://bertrandg.github.io/images/ng2_state/ngrx.png"}
                  {:name "DataScript" :experience 0.4 :type :FE :img "https://dbdb.io/media/logos/datascript.png"}

                  {:name "ASP.NET MVC" :experience 3 :type :BE :img "http://ezitech.pk/wp-content/uploads/2016/04/MVC.png"}
                  {:name "ASP.NET Web API" :experience 2 :type :BE :img "http://eduardopires.net.br/wp-content/uploads/2013/07/ASP.Net-Web-API.png"}
                  {:name "WCF" :experience 3 :type :BE :img "http://www.howcsharp.com/img/0/44/windows-communication-foundation-wcf-300x222.jpg"}
                  {:name "Entity Framework" :experience 2.5 :type :BE :img "https://www.asphostcentral.com/Images/EF_6.png"}
                  {:name "NodeJS" :experience 3 :type :BE :img "https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/07/1436439824nodejs-logo.png"}
                  {:name "Express" :experience 0.75 :type :BE :img "https://uploads.toptal.io/blog/category/logo/25/express_js.png"}
                  {:name "Django" :experience 0.5 :type :BE :img "https://www.djangoproject.com/s/img/logos/django-logo-negative.png"}
                  {:name "Djangae" :experience 0.5 :type :BE :img "https://pbs.twimg.com/profile_images/524210197808435200/bhYXBjo0.png"}
                  {:name "Socket.io" :experience 0.8 :type :BE :img "https://www.pubnub.com/blog/wp-content/uploads/2014/07/SOCKETIOICON.gif"}
                  {:name "SignalR" :experience 0.75 :type :BE :img "https://avatars3.githubusercontent.com/u/931666?v=3&s=200"}
                  {:name "Ring" :experience 1.5 :type :BE :img "https://avatars3.githubusercontent.com/u/1689840?v=3&s=200"}
                  {:name "Sente" :experience 0.25 :type :BE :img "https://camo.githubusercontent.com/0648bb98ec28429524dd6fcd384a326a5015ee26/68747470733a2f2f7777772e74616f656e73736f2e636f6d2f74616f656e73736f2d6f70656e2d736f757263652e706e67"}

                  {:name "NPM" :experience 3 :type :tool :img "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Npm-logo.svg/2000px-Npm-logo.svg.png"}
                  {:name "Yarn" :experience 1 :type :tool :img "https://tfrommen.de/wp-content/uploads/yarn-logo-400x400-1478627696.jpg"}
                  {:name "Bower" :experience 2 :type :tool :img "https://bower.io/img/bower-logo.png"}
                  {:name "NuGet" :experience 3 :type :tool :img "http://nuproj.net/images/NuGet.png"}
                  {:name "Grunt" :experience 2 :type :tool :img "http://vermilion1.github.io/presentations/grunt/images/grunt-logo.png"}
                  {:name "Gulp" :experience 1.25 :type :tool :img "https://raw.githubusercontent.com/gulpjs/artwork/master/gulp-2x.png"}
                  {:name "Make" :experience 0.5 :type :tool :img "https://files.digitalcitizen.info/logos/GNU/GNU-head-wallshadow.png"}
                  {:name "Babel" :experience 1 :type :tool :img "https://raw.githubusercontent.com/babel/logo/master/babel.png"}
                  {:name "Leiningen" :experience 2.2 :type :tool :img "https://leiningen.org/img/leiningen.jpg"}
                  {:name "Boot" :experience 0.1 :type :tool :img "http://boot-clj.com/assets/images/logos/boot-logo-3.png"}
                  {:name "Browserify" :experience 1 :type :tool :img "https://d21ii91i3y6o6h.cloudfront.net/gallery_images/from_proof/1647/large/1405586570/browserify-2-hexagon-sticker.png"}
                  {:name "Webpack" :experience 0.5 :type :tool :img "https://topheman.github.io/webpack-babel-starter/assets/974262647c82057b6078c432841a53ea.png"}
                  {:name "Rollup" :experience 0.5 :type :tool :img "https://i0.wp.com/wp.laravel-news.com/wp-content/uploads/2016/11/rollup1.png?resize=1400%2C709"}

                  {:name "SQL Server" :experience 3.5 :type :DataStore :img "http://cdn.warer.com/media/Microsoft-SQL-Server-2008-Express-logo.png"}
                  {:name "MongoDB" :experience 0.75 :type :DataStore :img "http://www.theodo.fr/uploads/blog//2015/11/mongodb.png"}
                  {:name "Google Cloud Datastore" :experience 0.5 :type :DataStore :img "http://www.datatechnology.co.uk/wp-content/uploads/2014/09/CloudDataStore_500px.png"}
                  {:name "Elastic" :experience 0.5 :type :DataStore :img "https://avatars0.githubusercontent.com/u/6764390?v=3&s=400"}
                  {:name "Firebase" :experience 0.5 :type :DataStore :img "https://lh3.googleusercontent.com/-whXBCDVxIto/Vz2Rsyz-UjI/AAAAAAAAiJc/UjvR-M2b9tY5SyKFkDY6Q_MbusEINRXkQ/w1024-h1024/Firebase_16-logo.png"}
                  {:name "Datomic" :experience 0.1 :type :DataStore :img "https://avatars0.githubusercontent.com/u/1478702?v=3&s=200"}

                  {:name "Karma" :experience 3 :type :Testing :img "https://avatars1.githubusercontent.com/u/3284117?v=3&s=400"}
                  {:name "Protractor" :experience 1.5 :type :Testing :img "http://coreboarder.com/blog/wp-content/uploads/2015/07/protractor-logo.png"}
                  {:name "NUnit" :experience 1.5 :type :Testing :img "https://www.jetbrains.com/teamcity/whatsnew/screenshots/91/logo-nunit.png"}
                  {:name "Specflow" :experience 1 :type :Testing :img "http://techtalk.github.io/SpecFlow/specflow.png"}
                  {:name "NSubstitute" :experience 1 :type :Testing :img "http://nsubstitute.github.io/images/nsubstitute-100x100.png"}
                  {:name "Moq" :experience 1 :type :Testing :img "https://avatars3.githubusercontent.com/u/1434934?v=3&s=400"}
                  {:name "Mocha" :experience 1.4 :type :Testing :img "https://upload.wikimedia.org/wikipedia/en/thumb/9/90/Mocha_(JavaScript_framework)_(logo).svg/1024px-Mocha_(JavaScript_framework)_(logo).svg.png"}
                  {:name "Chai" :experience 0.5 :type :Testing :img "https://camo.githubusercontent.com/431283cc1643d02167aac31067137897507c60fc/687474703a2f2f636861696a732e636f6d2f696d672f636861692d6c6f676f2e706e67"}
                  {:name "Sinon" :experience 0.5 :type :Testing :img "https://senchamarket-images-production.s3.amazonaws.com/uploads/screenshot/file/465/big_sinon_logo.png"}

                  {:name "Docker" :experience 1.75 :type :Misc :img "https://secure.gravatar.com/avatar/26da7b36ff8bb5db4211400358dc7c4e.jpg?s=512&r=g&d=mm"}
                  {:name "Graylog" :experience 0.75 :type :Misc :img "https://global-uploads.webflow.com/5ac133a161e365de41ae40d3/5ac133a161e36549ffae4144_logo-circle-graylog.png"}
                  {:name "Splunk" :experience 0.75 :type :Misc :img "https://community.dynatrace.com/community/download/attachments/25789254/logo_splunk_white_high.png?version=2&modificationDate=1365474920330&api=v2"}
                  {:name "RabbitMQ" :experience 0.75 :type :Misc :img "https://www.rabbitmq.com/img/rabbitmq_logo_strap.png"}

                  {:name "Azure" :experience 3.5 :type :Cloud :img "https://alligatortek.com/wp-content/uploads/2017/09/image115.png"}
                  {:name "AWS" :experience 1.5 :type :Cloud :img "https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Amazon_Web_Services_Logo.svg/2000px-Amazon_Web_Services_Logo.svg.png"}
                  {:name "Google Cloud Platform" :experience 0.75 :type :Cloud :img "http://www.averesystems.com/cmsFiles/relatedImages/logo_lockup_cloud_platform_icon_vertical.png"}
                  {:name "Rackspace" :experience 0.5 :type :Cloud :img "https://752f77aa107738c25d93-f083e9a6295a3f0714fa019ffdca65c3.ssl.cf1.rackcdn.com/icons/og-image.png"}
                  {:name "Heroku" :experience 0.25 :type :Cloud :img "https://a.slack-edge.com/bfaba/img/api/hosting_heroku.png"}
                  ]
   :projects "* [davewm.github.io](https://davewm.github.io) – my online CV, written using clojurescript, reagent, D3, and reagent-material-ui. Hosted on github pages.
* [Phonsole](https://www.phonsole.co.uk) – an app which allows you to use your phone to view console output from your PC. It’s split into 3 parts:
    * [Server](https://github.com/DaveWM/phonsole-server) – written in clojure, using ring and sente.
    * [Client](https://github.com/DaveWM/phonsole-client) – written in clojurescript, using re-frame and materialize css.
    * [CLI](https://github.com/DaveWM/phonsole-cli) – a nodejs app written in clojurescript. Published on npm as phonsole.
* [Reagent Material UI](https://github.com/DaveWM/reagent-material-ui) – a reagent wrapper for Material UI
* [ngWizard](https://github.com/DaveWM/ngWizard) – a wizard directive for angularjs.
* [Abode](https://github.com/DaveWM/Abode) – a web app for people living in a house share. It is an angularjs SPA with an asp.net web api 2 back end. I also published abode to the android app store as a phonegap hybrid app.
* [MyGit](https://github.com/DaveWM/MyGit) – a github client for windows phone 8.1
* [Larch](https://github.com/DaveWM/larch) - a ClojureScript web app framework loosely based on the Elm architecture
* [Monzo Web](https://github.com/DaveWM/monzo-cljs) - a progressive web app written in ClojureScript, using Larch and DataScript.
* [ATTyC](https://github.com/DaveWM/attyc) - a command line tool to type check AngularJS templates. 

I am a big fan of open source projects, and I have made some small contributions to:

* Sente
* Swagger-Spec
* Hickory
* Material UI
* Octokit.net
* Angular Material
* UI Bootstrap
* AngularStrap
* Angular-local-storage"})

(def bucket "dm-cv-api")

(def credentials
  (-> (let [cred-file (as-file "aws-credentials.json")]
        (if (.exists cred-file)
          (-> (slurp cred-file)
              (parse-string true))
          (->> [:access-key :secret-key]
               (map (fn [key] {key (env key)}))
               (apply merge))))
      (assoc :endpoint "eu-west-2")))

(amazonica.core/defcredential (:access-key credentials) (:secret-key credentials) "eu-west-2")

(defn upload-to-s3! [image key]
  (println "uploading" key)
  (let [out-stream (java.io.ByteArrayOutputStream.)]
    (ImageIO/write image "png" out-stream)
    (let [bytes     (.toByteArray out-stream)
          in-stream (java.io.ByteArrayInputStream. bytes)]
      (s3/put-object :bucket-name bucket
                     :key key
                     :input-stream in-stream
                     :metadata {:content-type "img/png"
                                :content-length (alength bytes)})
      (Thread/sleep 1000)
      (s3/set-object-acl bucket key {:grant-all [["AllUsers" "Read"]]})
      (.close in-stream)
      (.close out-stream)
      (println "upload complete" key))))

(defn format-s3-key [name]
  (-> name
      (s/replace "#" "sharp")
      (s/replace " " "-")
      s/lower-case
      (str ".png")))

(def cv-data
  (update raw-data :technologies #(->> %
                                       (map (fn [{:keys [img name] :as tech}]
                                              (let [s3-key (format-s3-key name)]
                                                (assoc tech :img (str "https://" bucket ".s3.amazonaws.com/" s3-key)))))
                                       doall)))


(defn resize-and-upload! [img name]
  (let [s3-key (format-s3-key name)]
    (-> (as-url img)
        (imagez/load-image)
        (imagez/resize 200)
        (upload-to-s3! s3-key))))

(defn upload-s3-images! []
  (->> (:technologies raw-data)
       (pmap (fn [{:keys [img name] :as tech}]
               (try
                 (resize-and-upload! img name)
                 (catch Exception e (println name (.getMessage e))))))
       doall))
