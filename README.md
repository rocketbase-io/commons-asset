# commons-asset

![logo](assets/commons-logo.svg)


[![Build Status](https://travis-ci.org/rocketbase-io/commons-asset.svg?branch=master)](https://travis-ci.org/rocketbase-io/commons-asset)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-asset/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-asset)
[![Maintainability](https://api.codeclimate.com/v1/badges/f09af44278b226f1f54c/maintainability)](https://codeclimate.com/github/rocketbase-io/commons-asset/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/f09af44278b226f1f54c/test_coverage)](https://codeclimate.com/github/rocketbase-io/commons-asset/test_coverage)

Add a simple asset service with basic features to your spring-boot applications. 

I've added a swagger api-documentation. You can find it within [src](./commons-asset-api/src/doc/swagger) of [swaggerHub](https://app.swaggerhub.com/apis/melistik/commons-asset/1.7.0)


**Features:**
* api and controller to handle asset-uploads
* works with jpeg, gif, png and files like pdf, excel, word and zip
* you can configure the allowed contentTypes via property *("asset.api.types")*
* 2 different storage implementations (mongo-grif-fs / aws-s3)
* embedded thumb service (for mongo-grif-fs) or thumbor via s3 connector
* java resource to communicate with api
* batch downloading urls and storing them
* intergrated [color-thief](https://github.com/SvenWoltmann/color-thief-java) in order to get primary and other colors from photo

## commons-asset-api

This module provides the DTOs and a client to communicate with the asset endpoints.
The main Objects are: **AssetReference** that is used to store in MongoDb in case of refrencing and **AssetRead** containing also the different preview urls.

***AssetReference sample:***

```json
{
    "id": "5b0f6bf23c5ded0ee5dcab57",
    "urlPath": "a/b/5/7/5b0f6bf23c5ded0ee5dcab57.jpg",
    "type": "JPEG",
    "meta": {
        "created": "2018-05-31T07:28:50.239",
        "originalFilename": "photo-1478472190689-fa020c20809a.jpeg",
        "fileSize": 1216844,
        "resolution": {
            "width": 4094,
            "height": 2730
        },
        "colorPalette": {
            "primary": "#395427",
            "colors": ["#ced5dd", "#80c9f3", "#74a043"]
        }
    }
}
```

With urlPath you can calculate your preview url. The logic is been implemented in AssetConverter.toRead(...)

During upload process the file is getting read so that some meta information get stored. In case of image also the resolution is getting stored...

## commons-asset-core

Containing an implementation for storing asset references...

| property           | default           | explanation                                                  |
| ------------------ | ----------------- | ------------------------------------------------------------ |
| asset.api.path     | /api/asset        | base path of assetController                                 |
| asset.api.types    | *All*             | you can shrink allowed Types (is a list property)            |
| asset.api.download | true              | you can disable endpoint                                     |
| asset.api.delete   | true              | when false no delete is possible                             |
| asset.api.batch    | true              | you can disable batch endpoint                               |
| asset.api.preview  | true              | should only been taken in case of mongo-storage<br />please consider also using outside caching |
| asset.api.detectResolution    | true              | you can disable image resolution detection                              |
| asset.api.detectColor    | true              | you can disable image colorThief                              |
| asset.api.baseUrl  | ""                | used for previewUrls in case of mongo-storage, will get used as fallback |
| asset.thumbor.host | *required for s3* | base url of your thumbor service                             |
| asset.thumbor.key  | *optional*        | secure your thumbor urls                                     |

## commons-asset-rest

Containing an all controllers for the rest implementation...

## commons-asset-s3

Containing a communication layer with s3 in order to use it as file storage.

You need to provide all required credentials that [spring-cloud](https://cloud.spring.io/spring-cloud-aws/spring-cloud-aws.html#_amazon_sdk_configuration) needs

```yaml
cloud:
  aws:
    credentials:
      accessKey: KEY
        secretKey: SECRET
        instanceProfile: true
      region:
        static: eu-central-1
```

| property          | default      | explanation                                                  |
| ----------------- | ------------ | ------------------------------------------------------------ |
| asset.s3.bucket   | **required** | bucket name where files should get stored                    |
| asset.s3.endpoint | *optional*   | allow to connect to replacements of aws s3<br />by for example  [minio](https://www.minio.io/) you can specify the endpoint |

### The MIT License (MIT)
Copyright (c) 2018 rocketbase.io

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.