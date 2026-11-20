![Snowcone](/docs/snowcone-logotype.png)

[![Clojure](https://img.shields.io/badge/clojure-babashka-%235881D8.svg?logo=clojure&logoColor=white)](https://book.babashka.org/)
[![GitHub](https://img.shields.io/badge/github-hosted-blue?logo=github&logocolor=white)](https://github.com/RyanFleck/Snowflake-Build-Tool)
[![Tests](https://github.com/RyanFleck/Snowflake-Build-Tool/actions/workflows/test.yml/badge.svg)](https://github.com/RyanFleck/Snowflake-Build-Tool/actions/workflows/test.yml)

<br />

A devops tool for assembling Snowflake SQL projects into deployment scripts, AKA "**Snowcone**".

Written in **Clojure** language - see the [babashka docs](https://manuals.ryanfleck.ca/clj/#babashka) and my [personal Clojure manual](https://manuals.ryanfleck.ca/clj/#babashka) for notes.

[github.com/RyanFleck/Snowflake-Build-Tool](https://github.com/RyanFleck/Snowflake-Build-Tool)

<br />

## Installation

See https://github.com/babashka/babashka#installation

On MacOS: `brew install borkdude/brew/babashka`

## Execution

To run, provide `bb` with the cloned classpath and main location.

```bash
bb -cp ~/vc/Snowflake-Build-Tool/src -m snowcone.main
```

## Development

Run `bb nrepl-server 0.0.0.0:1667` and then `M-x cider-connect-clj` to jack in.

## Attribution

- All code written by hand by [@RyanFleck](https://github.com/RyanFleck/Snowflake-Build-Tool)
- "Snowcone" logo generated with ChatGPT
