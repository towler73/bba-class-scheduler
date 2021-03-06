# BBA Class Scheduler

Use this program to find all possible schedules for your desired classes.

Class schedule can be found here: [Master Classes Schedule 2022-2023](https://docs.google.com/spreadsheets/u/4/d/e/2PACX-1vR5jen9QsaXcLbZbY81HE3LedUsR4UQjhBAnQCheFUp0DtK0b7bPurlVDJH8RjTFDNdzJ0uuIChWR28/pubhtml?gid=1396882618&single=true&urp=gmail_link)

## Installation

Install clojure
https://clojure.org/guides/getting_started

Clone this repository https://github.com/towler73/bba-class-scheduler

    $ git clone https://github.com/towler73/bba-class-scheduler.git

## Usage

To run the schedule you need 2 things:
 1. URL for the Master Schedule
 2. A file with your desired classes.  See desired-classes-sample.edn file in the root of source code for example.
    1. There are 3 keys you can define for each classes<br>
    `:class` Regex of class name<br>
    `:block` Regex of block<br>
    `:term` Reex of term

Run the project directly, via `:main-opts` (`-m org.bba.scheduler`):

    $ clojure -M:run-m <url> <desired-classes-sample.edn>

For Example:

    $ clojure -M:run-m "https://docs.google.com/spreadsheets/u/4/d/e/2PACX-1vR5jen9QsaXcLbZbY81HE3LedUsR4UQjhBAnQCheFUp0DtK0b7bPurlVDJH8RjTFDNdzJ0uuIChWR28/pubhtml?gid=1396882618&single=true&urp=gmail_link" desired-classes-sample.edn

Run the project's tests:

    $ clojure -T:build test

Run the project's CI pipeline and build an uberjar:

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/scheduler-standalone.jar

## Examples

...



## License

MIT License

Copyright (c) 2022 Brett T. Morgan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.