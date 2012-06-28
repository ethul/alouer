var request = require("request");
var libxmljs = require("libxmljs");
var deferred = require("JQDeferred");
var htmlparser = require("htmlparser");
var url = require("url");
var uuid = require("node-uuid");
var geo = require("geo");
var Nothing = function() {
    if(!(this instanceof Nothing)) {
        return new Nothing();
    }
};
var Just = function(a_0) {
    if(!(this instanceof Just)) {
        return new Just(a_0);
    }
    this._0 = a_0;
};
var feed = "http://ottawa.en.craigslist.ca/apa/index.rss";
var namespace = {
    "rdf": "http://purl.org/rss/1.0/",
    "dc": "http://purl.org/dc/elements/1.1/"
};
var id = function(a) {
    return a;
};
var deferredMonad = {
    "return": function(a) {
        return deferred.when(a);
    },
    "bind": function(ma, f) {
        var defer = deferred();
        ma.done(function(a) {
            return f(a).done(defer.resolve);
        });
        return defer.promise();
    }
};
var liftDeferred = function(f) {
    var defer = deferred();
    f(defer);
    return defer.promise();
};
var liftDeferredArray = function(dfds) {
    var defer = deferred();
    deferred.when.apply(null, dfds).done(function(_) {
        return defer.resolve(Array.prototype.slice.call(arguments, 0));
    });
    return defer.promise();
};
var runDeferred = function(defer, f) {
    return defer.done(f);
};
var maybeMonad = {
    "return": function(a) {
        return Just(a);
    },
    "bind": function(ma, f) {
        return (function() {
            if(ma instanceof Nothing) {
                return Nothing;
            } else if(ma instanceof Just) {
                var a = ma._0;
                return f(a);
            }
        })();
    }
};
var rdfTextFromEl = function(el) {
    return function(tag) {
        return el.get(("rdf:" + tag), namespace).text();
    };
};
var dcTextFromEl = function(el) {
    return function(tag) {
        return el.get(("dc:" + tag), namespace).text();
    };
};
var parseAddressHref = function(dom) {
    var isSmall = function(el) {
        return (el.type == "tag") && (el.raw == "small");
    };
    var isGoogleA = function(el) {
        return (el.type == "tag") && (el.name == "a") && el.raw.indexOf("google") != -1;
    };
    var extractLoc = function(href) {
        return url.parse(href, true).query.q.substr(5);
    };
    return dom.reduce(function(res, el) {
        return (function() {
            if(isSmall(el)) {
                return el.children.reduce(function(res, el) {
                    return (function() {
                        if(isGoogleA(el)) {
                            return Just(extractLoc(el.attribs.href));
                        } else {
                            return res;
                        }
                    })();
                }, Nothing());
            } else {
                return res;
            }
        })();
    }, Nothing());
};
var parseAddressCltag = function(dom) {
    var isBlurbs = function(el) {
        return el.raw.indexOf("ul class=\"blurbs\"") != -1;
    };
    var extractFromChild = function(el, res) {
        var containsGeo = function(el) {
            return el.raw.indexOf("CLTAG GeographicArea") != -1;
        };
        var extractGeo = function(el) {
            return el.raw.substr((el.raw.indexOf("=") + 1));
        };
        return (function() {
            if(el.children != undefined) {
                return el.children.reduce(function(res, el) {
                    return (function() {
                        if(containsGeo(el)) {
                            return Just(extractGeo(el));
                        } else {
                            return res;
                        }
                    })();
                }, Nothing());
            } else {
                return res;
            }
        })();
    };
    return dom.filter(isBlurbs).reduce(function(res, el) {
        return el.children.reduce(function(res, el) {
            var value = extractFromChild(el, res);
            return (function() {
                if(value instanceof Just) {
                    var a = value._0;
                    return Just(a);
                } else if(value instanceof Nothing) {
                    return res;
                }
            })();
        }, Nothing());
    }, Nothing());
};
var parseDom = function(html) {
    var handler = new(htmlparser).DefaultHandler(id);
    new(htmlparser).Parser(handler).parseComplete(html);
    return handler.dom;
};
var parseAddress = function(html) {
    var dom = parseDom(html);
    var fromHref = parseAddressHref(dom);
    return (function() {
        if(fromHref instanceof Nothing) {
            return parseAddressCltag(dom);
        } else if(fromHref instanceof Just) {
            var a = fromHref._0;
            return Just(a);
        }
    })();
};
var parseToApt = function(item) {
    var dc = dcTextFromEl(item);
    var rdf = rdfTextFromEl(item);
    var addr = parseAddress(rdf("description"));
    return {
        "id": uuid.v4(),
        "title": rdf("title"),
        "description": rdf("description"),
        "url": rdf("link"),
        "posted": dc("date"),
        "address": addr
    };
};
var geocode = function(apt) {
    return liftDeferred(function(defer) {
        return (function() {
            if(apt.address instanceof Nothing) {
                return defer.resolve(((function(__l__, __r__) {
            var __o__ = {}, __n__;
            for(__n__ in __l__) {
                __o__[__n__] = __l__[__n__];
            }
            for(__n__ in __r__) {
                __o__[__n__] = __r__[__n__];
            }
            return __o__;
        })(apt, {
            "lat": 0,
            "lng": 0,
            "address": ""
        })));
            } else if(apt.address instanceof Just) {
                var a = apt.address._0;
                return geo.geocoder(geo.google, a, false, function(a, lat, lng, z) {
            return defer.resolve(((function(__l__, __r__) {
                var __o__ = {}, __n__;
                for(__n__ in __l__) {
                    __o__[__n__] = __l__[__n__];
                }
                for(__n__ in __r__) {
                    __o__[__n__] = __r__[__n__];
                }
                return __o__;
            })(apt, {
                "lat": lat,
                "lng": lng,
                "address": a
            })));
        });
            }
        })();
    });
};
var apartments = function(f) {
    return runDeferred(((function(){
        var __monad__ = deferredMonad;
        
        return __monad__.bind(liftDeferred(function(defer) {
            return request(feed, function(_, _, body) {
                return defer.resolve(body);
            });
        }), function(body) {
            
            return __monad__.bind(deferredMonad.return(libxmljs.parseXmlString(body)), function(doc) {
                
                return __monad__.bind(deferredMonad.return(doc.root().find("rdf:item", namespace)), function(items) {
                    
                    return __monad__.bind(deferredMonad.return(items.map(parseToApt)), function(apts) {
                        
                        return __monad__.bind(liftDeferredArray(apts.map(geocode)), function(geos) {
                            
                            return __monad__.return(geos);
                        });
                    });
                });
            });
        });
    })()), f);
};
exports["apartments"] = apartments;;
