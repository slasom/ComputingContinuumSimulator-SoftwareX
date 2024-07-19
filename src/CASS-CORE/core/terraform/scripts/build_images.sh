echo "Executing build_images.sh"
cd "./docker/"

dirs=$(ls .)
for dir in $dirs;do
cd $dir
docker build -t pascal/$dir .
cd ..
done
cd ..