[![Snowcone](/docs/snowcone-logotype.png)](https://github.com/RyanFleck/Snowflake-Build-Tool)

[![Clojure](https://img.shields.io/badge/clojure-babashka-%235881D8.svg?logo=clojure&logoColor=white)](https://book.babashka.org/)
[![GitHub](https://img.shields.io/badge/github-hosted-blue?logo=github&logoColor=white)](https://github.com/RyanFleck/Snowflake-Build-Tool)
[![Tests](https://github.com/RyanFleck/Snowflake-Build-Tool/actions/workflows/test.yml/badge.svg)](https://github.com/RyanFleck/Snowflake-Build-Tool/actions/workflows/test.yml)
[![Licence - GNU GPL V3](https://img.shields.io/badge/license-GNU/GPLv3-red?logo=gnu&logoColor=white)](https://www.gnu.org/licenses/gpl-3.0.en.html)
[![Handwritten](https://img.shields.io/badge/authorship-handwritten-purple?logo=gnuemacs&logoColor=white)](https://ryanfleck.ca/about/)

<br />

**Snowcone**: A devops tool for assembling Snowflake SQL projects into deployment scripts. Written in **Clojure** language - see the [babashka docs](https://manuals.ryanfleck.ca/clj/#babashka) and my [personal Clojure manual](https://manuals.ryanfleck.ca/clj/#babashka) for notes.

Snowcone can build two different project types in two different modes.

**Build Matrix**:

|                    | **File-Based**                    | **Branch-Based**               |
|:-------------------|:----------------------------------|:-------------------------------|
| **SQL Files**      | Combined file in build/<env>      | Combined file in build branch  |
| **Data Notebooks** | Structure retained in build/<env> | Root structure in build branch |

Hosted at [github.com/RyanFleck/Snowflake-Build-Tool](https://github.com/RyanFleck/Snowflake-Build-Tool)

<br />

## Installation

See https://github.com/babashka/babashka#installation

On MacOS: `brew install borkdude/brew/babashka`

Once the `bb` executable is present on the system, the program can be run.

## Execution

To run, provide `bb` with the cloned classpath and main location.

```bash
bb -cp ~/vc/Snowflake-Build-Tool/src -m snowcone.main
```

## Development

Run `bb nrepl-server 0.0.0.0:1667` and then `M-x cider-connect-clj` to jack in.

## Attribution

- All code written by hand by [@RyanFleck](https://github.com/RyanFleck)
- "Snowcone" logo generated with ChatGPT
