# 3. Use Bitrise for CI

Date: 2019-06-04

## Status

Accepted

## Context

We need an easy way to integrate and test out code that is fast and reliable.

## Decision

We choose Bitrise because it came from a suggestion from more senior devs and provides an easy interface to manage the workflows with good support for Android apps and testing.
It also allows us to notify users in a easy way and with different roles. 

## Consequences

Bitrise does not allow us to use Pipelines-as-code 100% of the way. There is some configuration that needs to be done of the web workflow editor.
Instructions followed to setup: https://devcenter.bitrise.io/tips-and-tricks/use-bitrise-yml-from-repository/

It was very easy to setup UI testing as the default configurations of the steps are already working 
