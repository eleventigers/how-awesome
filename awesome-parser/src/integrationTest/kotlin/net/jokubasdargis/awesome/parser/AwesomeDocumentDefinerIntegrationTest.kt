package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.Host
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.Test

class AwesomeDocumentDefinerIntegrationTest : BaseIntegrationTest() {

    companion object {
        private val GITHUB = Host.from("github.com")!!

        private val RXJAVA_LINKS = setOf(
                "https://github.com/eleventigers/awesome-rxjava#awesome-rxjava-",
                "https://github.com/sindresorhus/awesome",
                "http://reactivex.io/",
                "https://github.com/ReactiveX/RxJava",
                "https://github.com/eleventigers/awesome-rxjava#bindings",
                "https://github.com/ReactiveX/RxAndroid",
                "https://github.com/JakeWharton/RxBinding",
                "https://github.com/f2prateek/rx-preferences",
                "https://github.com/tbruyelle/RxPermissions",
                "https://github.com/square/sqlbrite",
                "https://github.com/mcharmas/Android-ReactiveLocation",
                "https://github.com/pwittchen/ReactiveNetwork",
                "https://github.com/pwittchen/ReactiveSensors",
                "https://github.com/hzsweers/RxPalette",
                "https://github.com/davidmoten/rxjava-jdbc",
                "https://github.com/davidmoten/rxjava-file",
                "https://github.com/pakoito/RxTuples",
                "https://github.com/blipinsk/RxAnimationBinding",
                "https://github.com/eleventigers/awesome-rxjava#utilities",
                "https://github.com/ReactiveX/RxJavaAsyncUtil",
                "https://github.com/ReactiveX/RxJavaJoins",
                "https://github.com/ReactiveX/RxJavaMath",
                "https://github.com/ReactiveX/RxJavaString",
                "https://github.com/ReactiveX/RxJavaComputationExpressions",
                "https://github.com/davidmoten/rxjava-extras",
                "https://github.com/pakoito/RxActions",
                "https://github.com/JakeWharton/RxRelay",
                "https://github.com/android10/frodo",
                "https://github.com/pakoito/RxPartialApplication",
                "https://github.com/pakoito/RxCurrying",
                "https://github.com/eleventigers/rxeither",
                "https://github.com/JakeWharton/RxReplayingShare",
                "https://github.com/pakoito/RxFunctions",
                "https://github.com/eleventigers/awesome-rxjava#testing",
                "https://github.com/ribot/assertj-rx",
                "https://github.com/novoda/rxpresso",
                "https://github.com/eleventigers/awesome-rxjava#samples",
                "https://github.com/kaushikgopal/RxJava-Android-Samples",
                "https://github.com/eleventigers/awesome-rxjava#articles",
                "http://staltz.com/rx-glitches-arent-actually-a-problem.html",
                "http://blog.danlew.net/2016/01/25/rxjavas-repeatwhen-and-retrywhen-explained/",
                "http://tomstechnicalblog.blogspot.co.uk/2016/03/rxjava-problem-with-subjects.html",
                "https://github.com/eleventigers/awesome-rxjava#tools",
                "http://rxmarbles.com/",
                "https://github.com/eleventigers/awesome-rxjava#community",
                "http://groups.google.com/d/forum/rxjava",
                "http://stackoverflow.com/search?q=rx-java",
                "http://twitter.com/RxJava",
                "https://gitter.im/ReactiveX/RxJava",
                "https://github.com/ReactiveX/RxJava/issues",
                "https://github.com/eleventigers/awesome-rxjava#license",
                "https://creativecommons.org/publicdomain/zero/1.0/",
                "http://jokubasdargis.net/",
                "https://github.com/eleventigers/awesome-rxjava")
                .map { Link.from(it) }
    }

    @Test fun defineRxJava() {
        val document = document("awesome-rxjava.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("https://github.com/eleventigers/awesome-rxjava"))

        assertThat(definitions).hasSize(2)

        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    assertThat(it()).hasSize(54)
                    assertThat(it.ofHost(GITHUB)).hasSize(43)
                    assertThat(it.notOfHost(GITHUB)).hasSize(11)
                    assertThat(it.identified()).hasSize(54)
                    assertThat(it.invalid()).hasSize(0)
                    assertThat(it()).containsExactlyElementsIn(RXJAVA_LINKS)
                }
                is DocumentDefinition.LinkDefinitions -> {
                    assertThat(it()).hasSize(158)
                }
            }
        }
    }

    @Test fun defineAwesome() {
        val document = document("awesome.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("https://github.com/sindresorhus/awesome"))

        assertThat(definitions).hasSize(2)

        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    assertThat(it()).hasSize(472)
                    assertThat(it.ofHost(GITHUB)).hasSize(464)
                    assertThat(it.notOfHost(GITHUB)).hasSize(8)
                    assertThat(it.identified()).hasSize(472)
                    assertThat(it.invalid()).hasSize(0)
                }
                is DocumentDefinition.LinkDefinitions -> {
                    val linkDefinitions = it()
                    assertThat(linkDefinitions).hasSize(1425)
                    val relationships = linkDefinitions
                            .filter {
                                it is LinkDefinition.Relationship
                            }
                            .map {
                                it as LinkDefinition.Relationship
                            }
                    assertThat(relationships).hasSize(482)
                }
            }
        }
    }

    @Test fun defineSwiftEducation() {
        val document = document("awesome-swift-education.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("https://github.com/hsavit1/Awesome-Swift-Education"))

        assertThat(definitions).hasSize(2)

        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    assertThat(it()).hasSize(1359)
                    assertThat(it.ofHost(GITHUB)).hasSize(303)
                    assertThat(it.notOfHost(GITHUB)).hasSize(1056)
                    assertThat(it.identified()).hasSize(1359)
                    assertThat(it.invalid()).hasSize(0)
                }
                is DocumentDefinition.LinkDefinitions -> {
                    assertThat(it()).hasSize(2949)
                }
            }
        }
    }

    @Test fun defineDropwizard() {
        val document = document("awesome-dropwizard.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("https://github.com/stve/awesome-dropwizard#readme"))

        assertThat(definitions).hasSize(2)

        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    assertThat(it()).hasSize(96)
                    assertThat(it.ofHost(GITHUB)).hasSize(69)
                    assertThat(it.notOfHost(GITHUB)).hasSize(27)
                    assertThat(it.identified()).hasSize(96)
                    assertThat(it.invalid()).hasSize(0)
                }
                is DocumentDefinition.LinkDefinitions -> {
                    assertThat(it()).hasSize(282)
                }
            }
        }
    }

    @Test fun defineTrapLinks() {
        val document = document("trap-links.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("http://localhost"))

        assertThat(definitions).hasSize(2)

        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    assertThat(it()).hasSize(5)
                    assertThat(it.identified()).hasSize(4)
                    assertThat(it.invalid()).hasSize(1)
                    assertThat(it.ofHost(GITHUB)).hasSize(0)
                    assertThat(it.notOfHost(GITHUB)).hasSize(5)
                }
                is DocumentDefinition.LinkDefinitions -> {
                    assertThat(it()).hasSize(12)
                }
            }
        }
    }

    @Test fun defineMetamodeling() {
        val document = document("metamodeling.html")
        val definer = AwesomeDocumentDefiner.create(document)
        val definitions = definer(Link.from("https://en.wikipedia.org"))

        assertThat(definitions).hasSize(0)
    }
}
