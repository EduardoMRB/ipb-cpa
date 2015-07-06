# ipb-cpa

Website for the First Presbiteryan Church of CPA IV

## Getting Started

1. Start the application: `lein run-dev` (all dependencies will be installed)
1. Go to [localhost:8080](http://localhost:8080/) to see the home page

## JavaScript dependencies

The project comes with `package.json` and `bower.json` specifiyng the JS
dependencies. Run the following commands to install them.

```shell
$ npm install
$ bower install
```

> You must have node, npm and gulp globally installed.

After the installation, run `$ gulp` to compile assets and watch for changes.

## Migrations and Env variables

Follow to model of `lein-env.example` to create `lein.env` conforming to your
environment.

Go to `project.clj`, on `:ragtime` key, change the `:database` string to your
database credentials. After the previous step is done, you can run the migrations with
`lein ragtime migrate`.

## Compile ClojureScript

This project uses figwheel. To run it use: `$ lein figwheel` or `$ rlwrap lein
figwheel` if you have `rlwrap` installed.
