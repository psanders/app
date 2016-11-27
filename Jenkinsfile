node {

    // Images
    def registry                    = "gcr.io"
    def app                         = "fonoster-app"
    def fnapp_img                   = "$registry/$app/fnapp"      // Main component of Fonoster App
    def fnast_img                   = "$registry/$app/fnast"      // Asterisk image
    def fnmongodb_img               = "$registry/$app/fnmongodb"  // MongoDB image
    def tag                         = "1.0.${env.BUILD_NUMBER}"
    def atk_version                 = "1.0.5"
    def fncd                        = "fonoster/cd"
    def mongo_disk                  = "disk0001"
    def nfs_disk                    = "disk0002"
    def fncd_build                  = "docker build -t $fncd:latest ."
    def docker_cmd                  = "docker run -v /var/run/docker.sock:/var/run/docker.sock $fncd:latest"
    def push_img_cmd                = "$docker_cmd sh /opt/fn/scripts/push_image.sh"
    def deploy_prod_cmd             = "$docker_cmd sh /opt/fn/scripts/deploy.sh prod ${tag}"
    def deploy_qa_cmd               = "$docker_cmd sh /opt/fn/scripts/deploy.sh qa ${tag}"
    def snapshot_mongo_disk_cmd     = "$docker_cmd sh /opt/fn/scripts/disk_snapshot.sh $mongo_disk ${env.BUILD_NUMBER}"
    def snapshot_nfs_disk_cmd       = "$docker_cmd sh /opt/fn/scripts/disk_snapshot.sh $nfs_disk ${env.BUILD_NUMBER}"
    def build_fn                    = "$docker_cmd mvn clean verify -Pit -Dskip.docs=false -Ddocker.image.tag=${tag}"

    stage('Cleanup & Checkout') {
        try {
            sh "docker rm \$(docker ps -a -q)"
            sh "docker rm -v \$(docker ps -a -q -f status=exited)"
            sh "docker rmi \$(docker images -f \"dangling=true\" -q)"
        } catch (err) {
            echo "Nothing to clean"
        }
        deleteDir()
        checkout scm
    }

    stage('Prep') {
        retry(3) {
            sh "$fncd_build"                                  // Build Fonoster CD image
        }
    }

    stage('Build') {
        sh "$build_fn"
    }
}
