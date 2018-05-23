# commons-asset

![logo](assets/commons-logo.svg)


[![Build Status](https://travis-ci.org/rocketbase-io/commons-asset.svg?branch=master)](https://travis-ci.org/rocketbase-io/commons-asset)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-asset/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-asset)


Add a simple asset service with basic features to your spring-boot applications. 

**Features:**
* api and controller to handle asset-uploads
* works with jpeg, gif, png and as file pdf + zip
* 2 different storage implementations (mongo-grif-fs / s3)
* embedded thumb service (for mongo-grif-fs) or thumbor via s3 connector
* java resource to communicate with api
* combinable with commons-auth to protect endpoints

## commons-asset-api

This module provides the DTOs and a client to communicate with the asset endpoints.

## commons-asset-core

Containing an implementation for storing asset references...

## commons-asset-s3

Containing a communication layer with s3 in order to use it as file storage.

### The MIT License (MIT)
Copyright (c) 2018 rocketbase.io

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.