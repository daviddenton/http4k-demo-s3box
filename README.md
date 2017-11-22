# http4k demo: S3Box 

[![build status](https://travis-ci.org/daviddenton/http4k-demo-s3box.svg?branch=master)](https://travis-ci.org/daviddenton/http4k-demo-s3box.svg?branch=master)

This is a simple Dropbox clone built with [**http4k**](https://http4k.org) and deployed to Heroku through a fully CD pipeline run on Travis. the full application is <100 lines of code (when imports are excluded).

It uses the following [**http4k**](https://http4k.org) modules and features:

- http4k core `http4k-core`
- Jetty server module `http4k-server-jetty`
- Apache HTTP client `http4k-client-apache`
- AWS `http4k-aws` <-- This replaces the Java AWS SDK.
- Handlebars templating `http4k-template-handlebars`
- http4k multipart forms `http4k-multipart`

In action:
<img src="https://github.com/daviddenton/http4k-demo-s3box/raw/master/screenshot.png"/>

## Pre-requisites

Required environment variables:
```
CREDENTIALS=<user>:<password>                   // for basic auth on the site
S3_REGION=<s3-region>                           // eg. us-east-1
AWS_CREDENTIALS=<awsAccessKey>:<awsSecretKey>   // AWS access key with full S3 access
AWS_BUCKET=<bucket>                             // existing AWS bucket
```

## Running it locally

Set the above environment variables and run the `S3BoxLauncher` class. The app will be available on [http://localhost:5000](http://localhost:5000)
