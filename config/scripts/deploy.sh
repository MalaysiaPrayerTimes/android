if [ "$TRAVIS_TAG" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
    ./gradlew generateProdReleasePlayResources
    # ./gradlew publishListingProdRelease -PPLAY_STORE_TRACK=rollout
elif [ "$TRAVIS_BRANCH" == "develop" ]; then
    ./gradlew generateBetaReleasePlayResources
    # ./gradlew publishListingBetaRelease
fi
