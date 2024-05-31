# Remote Require for Clojure

Require any Clojure snippet on-the-fly from anywhere in the Internet!

## Rationale

For example, James Reeves wrote a library of useful utility functions, [medley](https://github.com/weavejester/medley). But you don’t need everything from it, you need just `assoc-some`. It would be a waste to include a whole library just to use a single function!

Meet Remote Require!

## Usage

0. Add `remote-require` dependency to your project (just once):

```clojure
io.github.tonsky/remote-require {:git/sha "c9027682287f48cd89450839b18e6c69758fc343"}
```

1. Go to the file you are interested in, e.g. [src/medley/core.cljc](https://github.com/weavejester/medley/blob/master/src/medley/core.cljc)
2. Find a function that you need, e.g. `assoc-some`
3. Include it!

```clojure
(require '[remote-require.core :as rr])

(rr/from "https://github.com/weavejester/medley/master/src/medley/core.cljc"
  :require [assoc-some])
```

That’s it! Now you can use it freely:

```clojure
(assoc-some {}
  :a 1
  :b nil
  :c 3) ; => {:a 1, :c 3}
```

## Examples

```clojure
; File in Github repo
(rr/from "https://github.com/weavejester/medley/d1e00337cf6c0843fb6547aadf9ad78d981bfae5/src/medley/core.cljc"
  :require [dissoc-in assoc-some regexp?])

; Gist
(rr/from "https://gist.githubusercontent.com/calebphillips/240dd6162aefb77584a88249b009fbe6/raw/d8b5a865e7a58bab7da48493a64db848d4f8c0f5/cal.clj"
  :require [left-pad right-pad])

; Twitter
(rr/from "https://twitter.com/nikitonsky/status/1584629264909225984"
  :require [zip now clamp between?])
```

## Highlights

- Works directly from the REPL, no restart required
- Caches fetched pages locally (in `~/.clj-remote-require`), so will work offline after initial fetch
- Can import functions, variables and macros
- Only imports what you explicitly asked for, stripping away everything else
- Allows reasonable amount of extra text in sources (as long as it can’t be confused with Clojure code)

## Supported sources

- Github
- Gist (only RAW output)
- Twitter
- Anything else that serves “clean enough” HTML page

## How does it work?

What happens under the hood is:

- URL is fetched
- Resulting page is cached locally for future repeated imports
- Clojure part of HTML is parsed
- `(defn <your-symbol> ...)` is found
- Macro outputs `defn` content into current namespace
- If something goes wrong, exception is thrown

## Best practices

- Try to refernce immutable sources like Github + SHA or Twitter
- In Github, press `Y` to switch from `master` to current SHA

## Limitations

- Does not resolve dependencies or namespace aliases
- If one fn depends on the other, importing both should work though

```clojure
; Github repo, `map-keys` depends on `editable?` and `reduce-map`
(rr/from "https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
  :require [editable? reduce-map map-keys])
```

- Can’t import functions with the same name from different sources (one will override another)
