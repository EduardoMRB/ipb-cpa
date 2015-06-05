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

## Compile ClojureScript

Only supported in dev environment `$ lein cljsbuild auto dev`
