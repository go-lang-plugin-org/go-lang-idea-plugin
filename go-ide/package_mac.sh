GOSOURCEPATH=/Volumes/Bubble/projects/go
IDEASOURCEPATH=/Volumes/Bubble/projects/idea
PLUGINSOURCEPATH=/Volumes/Bubble/projects/google-go-lang-idea-plugin

# Build the go sdk
cd $GOSOURCEPATH/src
./all.bash
rm -r $IDEASOURCEPATH/build/conf/mac/go-sdk
cp -r $GOSOURCEPATH $IDEASOURCEPATH/build/conf/mac/go-sdk

# Copy Go logo and about images
cp $PLUGINSOURCEPATH/go-ide/resources/idea_community_about.png $IDEASOURCEPATH/community-resources/src/
cp $PLUGINSOURCEPATH/go-ide/resources/idea_community_logo.png $IDEASOURCEPATH/community-resources/src/

cd $IDEASOURCEPATH
ant build
VERSION=$(cat build.txt)
cp $IDEASOURCEPATH/out/artifacts/ideaIC-$VERSION.mac.zip $PLUGINSOURCEPATH/go-ide
cd $PLUGINSOURCEPATH/go-ide
rm -r Community\ Edition-IC-$VERSION.app
unzip ideaIC-$VERSION.mac.zip

unzip ../google-go-language.zip -d Community\ Edition-IC-$VERSION.app/plugins
rm ideaIC-$VERSION.mac.zip

echo "Now open up dmgCreator to make the dmg."