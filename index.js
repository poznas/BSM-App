const functions = require('firebase-functions');
const admin = require('firebase-admin')

admin.initializeApp(functions.config().firebase)

const rootRef = admin.database().ref()

//Creates user profile in /users/{uid}
exports.createUserProflie
    = functions.auth.user().onCreate( event => 
    {
        const uid = event.data.uid
        const email = event.data.email
        const photoUrl = event.data.photoURL || 'http://i.kafeteria.pl/0991f9c6631ca79a8bb5b5199b2c39df1fc77dc4'

        const newUserRef = rootRef.child(`/users/${uid}`)

        console.log('Creating new user profile: ', uid )

        return newUserRef.set({
            photoUrl: photoUrl,
            email: email
            })
    })

//Detetes user profile in /users/{uid}
exports.cleanupUserData 
    = functions.auth.user().onDelete( event => 
    {
        const uid = event.data.uid
        console.log('Deleting user profile: ', uid )

        return rootRef.child(`/users/${uid}/team`).once('value')
        .then( result => {

            const team = result.val()  
            return rootRef.child(`/TEAMS/${team}/${uid}`).set(null)
        })
        .then( () => {
            return rootRef.child(`/users/${uid}`).set(null)
        })
    })

//Adds previously hardcoded details from /UserDetails/{emailś}
//Gives proper team /TEAMS/ -sensum, -cormeum, -mutinium, -judge
exports.handleNewUser
    = functions.database.ref('/users/{uid}').onWrite( event => 
    {
        const uid = event.params.uid

        return rootRef.child(`/users/${uid}`).once('value')
        .then( result => 
        {
            currentUser = result.val()

            if( !currentUser ){ return console.log( 'User ', uid, ' no longer exists.' )}
            if( currentUser.label ){ return console.log( 'User ', uid, ' handled already.' )}

            const emailś = currentUser.email.replace(/\./g, 'ś')
            return rootRef.child(`/UserDetails/${emailś}`).once('value')
            .then( result => 
            {
                const UD = result.val()
                if( !UD ){ 
                    return console.log('User ', uid, 'is unauthorized')
                }
                else
                {
                    if( UD.label == "judge" ){
                        prm1 = rootRef.child(`/users/${uid}`).update({
                            label : UD.label
                        })
                    } else {
                        prm1 = rootRef.child(`/users/${uid}`).update({
                            displayName : UD.displayName,
                            label : UD.label,
                            gender : UD.gender,
                            facebook : UD.facebook,
                            team : UD.team
                        })
                    }
                    prm2 = rootRef.child(`/TEAMS/${UD.team}/${uid}`).set('true')

                    return Promise.all([ prm1, prm2 ])
                    .then( () => 
                    {
                        return console.log( 'User ', uid, UD.displayName, ' is ', UD.label, 'from', UD.team )
                    })
                }
            })
        })
    })

//Decide if newly edited(added) report in /Reports/ 
//should go to /pendingReports/ and /requireProfRate/
exports.HandleNewReport
    = functions.database.ref('Reports/{rpid}').onWrite( event => 
    {
        const rpid = event.params.rpid
        const rpidData = event.data.val()

        if( !rpidData ){ return console.log('Report ', rpid, 'no longer exists.')}
        if( rpidData.valid == false ){ return console.log('Report ', rpid, 'is not valid.')}
        if( rpidData.rated ){ return console.log('Report ', rpid, 'is already rated.')}

        return rootRef.child(`/SideMissionsProperities/${rpidData.sm_name}`).once('value')
        .then( result => 
        {
            const SMPSnap = result.val()
            if( SMPSnap == null ){ return console.log( rpidData.sm_name, " doesnt exist in database.")}
            const reqProf = SMPSnap.requireProfessor

            return rootRef.child(`/requireProfRate/${rpid}`).once('value')
            .then( result => 
            {
                const inReqProfRare = result.val()
                if( reqProf )
                {
                    return rootRef.child(`/FinalReportRate/${rpid}/requireProfessor`).once('value')
                    .then( result => 
                    {
                        const finalReqProf = result.val()
                        if( finalReqProf ){ return console.log( rpid, rpidData.sm_name, " has alreary been rated by professor.") }
                        if( inReqProfRare ){ return console.log( rpid, rpidData.sm_name, " already waits to be rated by professor.") }

                        return rootRef.child(`/users/${rpidData.performing_user}`).once('value')
                        .then( result =>
                        {
                            userSnap = result.val()

                            prm1 = rootRef.child(`/requireProfRate/${rpid}`).update({
                                sm_name : rpidData.sm_name,
                                performing_user : userSnap.displayName,
                                user_photoUrl : userSnap.photoUrl,
                                timestamp : admin.database.ServerValue.TIMESTAMP,
                                post : SMPSnap.isPost
                            })
                            prm2 = rootRef.child(`FinalReportRate/${rpid}/requireProfessor`).set(true)

                            return Promise.all([prm1, prm2])
                        })
                        .then( () => 
                        {
                            return console.log( rpid, rpidData.sm_name, " successfully added to /requireProfRate/")
                        })
                    })
                }
                return rootRef.child(`FinalReportRate/${rpid}/requireProfessor`).set(false)
                .then( () => 
                {
                    return console.log( rpid, rpidData.sm_name, " is not required to be rated by professor.")
                })
            })
            .then( () => 
            {
                return rootRef.child(`/pendingReports/${rpid}`).once('value')
                .then( result => 
                {
                    if( result.val() ){ return console.log( rpid, rpidData.sm_name, " already waits to be rated by judges.") }

                    return rootRef.child(`/users/${rpidData.performing_user}`).once('value')
                    .then( result =>
                    {
                        userSnap = result.val()
                        return rootRef.child(`/pendingReports/${rpid}`).update({
                            sm_name : rpidData.sm_name,
                            performing_user : userSnap.displayName,
                            user_photoUrl : userSnap.photoUrl,
                            timestamp : admin.database.ServerValue.TIMESTAMP,
                            post : SMPSnap.isPost
                        })
                        .then( () => 
                        {
                            return console.log( rpid, rpidData.sm_name, " successfully added to /pendingReports/")
                        })
                    })
                })
            })
        })  
    })

//If number of children of /ReportRates/{rpid}
//  reaches defined targetQuantity 
//  function updates /FinalReportRate/{rpid}
//  with averaged rates and timestamp
//  also sets there ratedByJudges: true
//  then deletes rpid from /pendingReports/
//  and sets /Report/{rpid}/rated: true
exports.makeFinalReportRate
    = functions.database.ref('ReportRates/{rpid}').onWrite( event => 
    {
        const targetQuantity = 3
        const rpid = event.params.rpid
        const rpidData = event.data.val()
        return rootRef.child(`/FinalReportRate/${rpid}/ratedByJudges`).once('value')
        .then( result => 
        {
            if( result.val() ){ return console.log( rpid, 'has final rate already')}

            var currentAmount = 0
            for( var judge in rpidData ){
                currentAmount +=1
            }
            if( currentAmount < targetQuantity )
            {
                return console.log( rpid, 'has not enough rates')
            }
            FinalRate = {}
            var i = 0
            for( var judge in rpidData )
            {
                var rate = rpidData[judge]
                if( i == 0 ){
                    for( var prop in rate ){
                        FinalRate[prop] = 0
                    }
                }
                for( var prop in rate ){
                    FinalRate[prop] += rate[prop]
                }
                i += 1
            }
            for( var prop in FinalRate ){
                FinalRate[prop] = FinalRate[prop]/i
            }
            console.log( rpid, 'FinalRate', FinalRate )
            prm1 = rootRef.child(`FinalReportRate/${rpid}/properities`).update(FinalRate) 
            prm2 = rootRef.child(`FinalReportRate/${rpid}/timestamp`).set(admin.database.ServerValue.TIMESTAMP)

            return Promise.all([ prm1, prm2 ])
            .then( () => {
                return rootRef.child(`FinalReportRate/${rpid}/ratedByJudges`).set(true)
            })
            .then( () => {
                return rootRef.child(`pendingReports/${rpid}`).set(null)
            })
            .then( () => {
                return rootRef.child(`Reports/${rpid}/rated`).set(true)
            })
        })
    }) 

//couts points from /FinalReportRate/{rpid}
//and adds it to /ReportPoints/{rpid}
exports.addToReportPoints
    = functions.database.ref('FinalReportRate/{rpid}').onWrite( event => 
    {
        const rpid = event.params.rpid
        const FinRpSnap = event.data.val()
        // here's the change 12:20 / 19/07/2017
        if( !FinRpSnap ){
            return console.log( rpid, ' Report,s final rate no longer exist')
        }
        if( FinRpSnap.requireProfessor ){
            return console.log('Report ', rpid, 'still waits to be rated by professor')
        }
        if( FinRpSnap.ratedByJudges == false ){
            return console.log('Report ', rpid, 'still waits to be rated by judges')
        }

        return rootRef.child(`/Reports/${rpid}`).once('value')
        .then( result => 
        {
            const RPinfoSnap = result.val()
            if( RPinfoSnap.valid == false ){ return console.log('Report ', rpid, 'is not valid.')}

            prm1 = rootRef.child(`/users/${RPinfoSnap.performing_user}`).once('value')
            prm2 = rootRef.child(`/SideMissionsProperities/${RPinfoSnap.sm_name}/properities/płeć wykonawcy`).once('value')

            return Promise.all([ prm1, prm2 ])
            .then( results => 
            {
                const userSnap = results[0].val()
                const genderSnap = results[1].val()

                promises = []

                if( genderSnap )
                {
                    const genderValues = genderSnap.type.spinner
                    console.log(rpid, RPinfoSnap.sm_name, 'setting gender value to ', genderValues[userSnap.gender] )
                    prm1 =  rootRef.child(`FinalReportRate/${rpid}/properities/Płeć`).set(genderValues[userSnap.gender])
                    promises.push(prm1)
                }
                prm2 = console.log(rpid, RPinfoSnap.sm_name, ' processing...' )
                promises.push(prm2)
                
                return Promise.all(promises)
                .then( () => 
                {
                    return rootRef.child(`/SideMissionsProperities/${RPinfoSnap.sm_name}/equation`).once('value')
                    .then( result => 
                    {
                        const equation = result.val()
                        const prop = FinRpSnap.properities
                        eval(equation)
                    
                        proms = []
                        prm1 = rootRef.child(`/ReportPoints/${rpid}/points`).set(PKT)
                        prm2 = rootRef.child(`/ReportPoints/${rpid}/team`).set(userSnap.team)
                        prm3 = rootRef.child(`/ReportPoints/${rpid}/valid`).set(true)

                        return Promise.all(promises)
                        .then( () => 
                        {
                            return console.log(rpid, RPinfoSnap.sm_name,'|',userSnap.displayName, 'got', PKT, 'points for ',userSnap.team )
                        })
                    })
                })
            })
        })
    })

// if valid, sends points from /ReportPoints/ to /AllPoints/
exports.addReportPointsToAll
    = functions.database.ref('ReportPoints/{rpid}').onWrite( event => 
    {
        const rpid = event.params.rpid
        const rpPtsSnap = event.data.val()

        if( !rpPtsSnap ){ return console.log( rpid, 'Report no longer exist ')}
        if( rpPtsSnap.points <= 0 ){ return console.log( rpid, 'Report points are invalid ') }

        promises = []

        return rootRef.child(`Reports/${rpid}`).once('value')
        .then( result => 
        {
            const rpidSnap = result.val()
            if( !rpidSnap ){ return console.log( rpid, ' aborting: no such rpid in /Reports/')}

            return rootRef.child(`users/${rpidSnap.performing_user}`).once('value')
            .then( result => 
            {
                const userSnap = result.val()
                if( !userSnap ){ return console.log( rpid, ' aborting: invalid performing_user ')}

                return rootRef.child(`/SideMissionsProperities/${rpidSnap.sm_name}/isPost`).once('value')
                .then( result => 
                {
                    const isPostState = result.val()
                    if( isPostState == null ){ return console.log( rpid, ' aborting: invalid info in /SideMissionsProperities/')}

                    if( rpPtsSnap.valid == false )
                    {
                        prm1 = rootRef.child(`AllPoints/${rpid}`).set(null)
                        prm2 = console.log( rpid, 'is no longer valid --> deleting from /AllPoints/')
                        promises.push(prm1)
                        promises.push(prm2)
                    }
                    else
                    {
                        prm1 = rootRef.child(`AllPoints/${rpid}`).update({
                            points : rpPtsSnap.points,
                            team : rpPtsSnap.team,
                            label : "SM",
                            timestamp : rpidSnap.timestamp,
                            user_name : userSnap.displayName,
                            user_photo : userSnap.photoUrl,
                            isPost : isPostState
                        })
                        prm2 = console.log( rpid, 'updated in /AllPoints/')
                    }

                    return Promise.all(promises)
                    .then( () => 
                    {
                        return rootRef.child('AllPoints').once('value')
                        .then( result => 
                        {
                            PointsSnap = result.val()
                            return sumPoints(PointsSnap)
                        })
                    })
                })
            })
        })
    })


// if valid, sends points from /BetPoints/ to /AllPoints/
exports.addBetPointsToAll
    = functions.database.ref('BetPoints/{Bid}').onWrite( event => 
    {
        const Bid = event.params.Bid    // lol
        const BidSnap = event.data.val()

        if( !BidSnap ){ return console.log( Bid, 'Bet no longer exist ')}
        if( BidSnap.points <= 0 ){ return console.log( Bid, 'Bet points are invalid ') }

        promises = []

        if( BidSnap.valid == false )
        {
            for( var id in BidSnap.results ){
                prm1 = rootRef.child(`/AllPoints/${id}`).set(null)
                promises.push(prm1)
            }
            prm2 = console.log( Bid, 'is no longer valid --> deleting from /AllPoints/')
            promises.push(prm2)
        }
        else
        {
            var losser = ""
            var winner = ""
            for( var id in BidSnap.results )
            {
                result = Object.keys(BidSnap.results[id])[0]
                

                prm1 = rootRef.child(`/AllPoints/${id}`).update({
                    team : BidSnap.results[id][ result ],
                    label : "B",
                    timestamp : admin.database.ServerValue.TIMESTAMP,
                    info : BidSnap.info
                })
                if( result == 'win' ){
                    prm2 = rootRef.child(`/AllPoints/${id}/points`).set(BidSnap.points)
                    winner = BidSnap.results[id][ result ]
                }else{
                    prm2 = rootRef.child(`/AllPoints/${id}/points`).set(-1*BidSnap.points)
                    losser = BidSnap.results[id][ result ]
                }
                promises.push( prm1 )
                promises.push( prm2 )
            }
            for( var id in BidSnap.results )
            {
                promises.push( 
                    rootRef.child(`/AllPoints/${id}`).update({
                        losser : losser,
                        winner : winner
                    })
                )
            }     
        }
        return Promise.all(promises)
        .then( () => 
        {
            return rootRef.child('AllPoints').once('value')
            .then( result => 
            {
                PointsSnap = result.val()
                return sumPoints(PointsSnap)
            })
        })
    })


// if valid, sends points from /SpecialPoints/ to /AllPoints/
exports.addSpecialPointsToAll
    = functions.database.ref('/SpecialPoints/{Sid}').onWrite( event => 
    {
        const Sid = event.params.Sid
        const SidSnap = event.data.val()

        if( !SidSnap ){ return console.log( Sid, 'in SpecialPoints no longer exist ') }
        if( SidSnap.valid == false )
        {
            prm1 = rootRef.child(`/AllPoints/${Sid}`).set(null)
            prm2 = console.log( Sid, 'is no longer valid --> deleting from /AllPoints/')
        }
        else
        {
            prm1 = rootRef.child(`/AllPoints/${Sid}`).update({
                points : SidSnap.points,
                team : SidSnap.team,
                label : "S",
                timestamp : admin.database.ServerValue.TIMESTAMP,
                info : SidSnap.info
            })
            prm2 = console.log( Sid, '/SpecialPoints/ ---> /AllPoints/')
        }
        return Promise.all([ prm1, prm2 ])
        .then( () => 
        {
            return rootRef.child('AllPoints').once('value')
            .then( result => 
            {
                PointsSnap = result.val()
                return sumPoints(PointsSnap)
            })
        })
    })

// if valid, sends points from /MainCompetitionPoints/ to /AllPoints/
exports.addMCPointsToAll
    = functions.database.ref('/MainCompetitionPoints/{Mid}').onWrite( event => 
    {
        const Mid = event.params.Mid
        const MidSnap = event.data.val()

        promises = []

        if( !MidSnap ){ return console.log( Mid, 'in MainCompetitionPoints no longer exist ') }
        if( MidSnap.valid == false )
        {
            prm1 = rootRef.child(`/AllPoints/${Mid}`).set(null)
            prm2 = console.log( Mid, 'is no longer valid --> deleting from /AllPoints/')
        }
        else
        {
            prm1 = rootRef.child(`/AllPoints/${Mid}`).update({
                points : MidSnap.points,
                team : MidSnap.team,
                label : "MC",
                timestamp : admin.database.ServerValue.TIMESTAMP,
                name : MidSnap.name,
                info : MidSnap.info
            })
            prm2 = console.log( Mid, '/SpecialPoints/ ---> /AllPoints/')
        }
        return Promise.all([ prm1, prm2 ])
        .then( () => 
        {
            return rootRef.child('AllPoints').once('value')
            .then( result => 
            {
                PointsSnap = result.val()
                return sumPoints(PointsSnap)
            })
        })
    })


//updates /ReportPoints/valid according to /Reports/valid
exports.handleChangeOfReportValid
    = functions.database.ref('/Reports/{rpid}/valid').onWrite( event => 
    {
        currentValid = event.data.val()
        rpid = event.params.rpid

        return rootRef.child(`/ReportPoints/${rpid}/valid`).once('value')
        .then( result => 
        {
            if( result.val() == null ){
                return console.log( rpid, 'Cancel valid update: report is not fully rated' )
            }else{
                return rootRef.child(`/ReportPoints/${rpid}/valid`).set(currentValid)
            }
        })
    })
videoFormats = [
    ".avi", ".mp4", ".mov", ".wmv", ".3gp", ".mpg", ".flv"
]

//adds from /AllPoints/ to /Zongler/ if isPost
exports.addToZongler
    = functions.database.ref('/AllPoints/{rpid}').onWrite( event => 
    {
        const rpid = event.params.rpid
        const rpPtsSnap = event.data.val()
        if( !rpPtsSnap ){ 
            return Promise.all([
                console.log( rpid, 'in AllPoints no longer exist --> delete from /Zongler'),
                rootRef.child(`Zongler/${rpid}`).set(null)
            ]) 
        }
        if( rpPtsSnap.label != "SM" ){ return console.log( rpid, 'is not SM ')}
        if( !rpPtsSnap.isPost ){ return console.log( rpid, 'SM is not a Post ')}

        return rootRef.child(`Reports/${rpid}`).once('value')
        .then( result => 
        {
            rpidSnap = result.val()
            if( !rpidSnap ){ return console.log( rpid, 'in /Reports no longer exist ')}


            return rootRef.child(`Reports/${rpid}/mediaUrls`).once('value')
            .then( result => 
            {
                const mediaSnap = result.val()

                if( rpidSnap.valid == false )
                { 
                    prm1 = console.log( rpid, ' not valid --> delete from /Zongler')
                    prm2 = console.log( rpid, ' JP2GMD ')
                    prm3 = rootRef.child(`Zongler/${rpid}`).set(null)
                }
                else
                {
                    prm1 = console.log( rpid, ' adding to /Zongler')
                    prm2 = rootRef.child(`Zongler/${rpid}`).update({
                        author : rpPtsSnap.user_name,
                        authorPhotoUrl : rpPtsSnap.user_photo,
                        title : rpidSnap.text.title,
                        body : rpidSnap.text.body,
                        timestamp : rpidSnap.timestamp,
                    })
                    if( mediaSnap )
                    {
                        photo = null
                        video = null 

                        for( media in mediaSnap ){
                            for( var i=0; i<videoFormats.length; i++ ){
                                if( mediaSnap[media].orginalUrl.includes(videoFormats[i]) ){

                                    console.log( rpid, "index of ", videoFormats[i], ": ",
                                         mediaSnap[media].orginalUrl.indexOf(videoFormats[i]) )

                                    video = mediaSnap[media]
                                    break
                                }
                            }
                            if( !video ) {
                                photo = mediaSnap[media]
                            }  
                        }

                        if( video ){
                            prm3 = rootRef.child(`Zongler/${rpid}`).update({
                                thumbnailUrl : video.thumbnailUrl,
                                videoUrl : video.orginalUrl
                            })
                        }else if( photo ){
                            prm3 = rootRef.child(`Zongler/${rpid}`).update({
                                thumbnailUrl : photo.thumbnailUrl,
                                imageUrl : photo.orginalUrl
                            })
                        }
                    }else{
                        prm3 = console.log( rpid, ' has no media attached')
                    }
                }
                return Promise.all([prm1,prm2,prm3])
            })
        })
    })

// splits AllPoints into separated Lists for invidual team
// improves app performance

exports.splitAllPoints
    = functions.database.ref('/AllPoints').onWrite( event => 
    {
        const allPoints = event.data.val()
        if( !allPoints ){
        
            return Promise.all([
                console.log( 'AllPoints no longer exist --> remove splitted lists'),
                removeSplittedAllPoints
            ])
        }
        return Promise.all([removeSplittedAllPoints])
        .then( () => 
        {
            promises = []
            for( pointsId in allPoints ){
                promises.push(
                    rootRef
                    .child(allPoints[pointsId].team+'AllPoints')
                    .child(pointsId)
                    .set( allPoints[pointsId] )
                )
            }
            return Promise.all(promises)
            .then( () => 
            {
                return console.log('Splitting AllPoints succeed' )
            })
        })
    })

//removes products of splitting /AllPoints
function removeSplittedAllPoints()
{
    return Promise.all([
        rootRef.child('cormeumAllPoints').set(null),
        rootRef.child('sensumAllPoints').set(null),
        rootRef.child('mutiniumAllPoints').set(null)
    ])
}

//sums all kind of points from /AllPoints/
//  and updates /SCORES
function sumPoints(PointsSnap)
{
    if( !PointsSnap ){ return console.log( "abort counting")}
    scores = {
        sensum : 0,
        cormeum : 0,
        mutinium : 0
    }
    for( var Pid in PointsSnap )
    {
        scores[PointsSnap[Pid].team] += PointsSnap[Pid].points
    }
    return rootRef.child(`/SCORES`).update(scores)
}

exports.notifyJudgeAboutNewReport
    = functions.database.ref('/pendingReports/{rpid}').onWrite( event =>
    {
        const rpid = event.params.rpid
        if( !event.data.val() ){return console.log( rpid, 'in /pendingReports no longer exist ')}

        return rootRef.child(`ReportNotificationSent/${rpid}`).once('value')
        .then( result => 
        {
            if( result.val() == true ){
                return console.log( rpid, 'nalready notified judges ')
            }

            return rootRef.child(`ReportNotificationSent/${rpid}`).set(true)
            .then( () => 
            {
                return rootRef.child(`Reports/${rpid}`).once('value')
                .then( result => 
                {   
                    const reportData = result.val()
                    if( !reportData ){return console.log( rpid, 'in /Reports no longer exist ')}

                    return rootRef.child(`/users/${reportData.performing_user}`).once('value')
                    .then( result => 
                    {
                        const userData = result.val()
                        if( !userData ){return console.log( rpid, 'Performing user: ',reportData.performing_user ,' no longer exist ') }

                        messageBody = userData.displayName+' - '+reportData.sm_name

                        const payload = {
                            notification: {
                                title: "Oceń Meldunek",
                                body: messageBody,
                                icon: userData.photoUrl,
                                sound: "default"
                            }
                        }

                        const options = {
                            priority: "high",
                            timeToLive: 60*60*24
                        }

                        return admin.messaging().sendToTopic( "reportsToJudge", payload, options )
                    })
                })
            })
        })  
    })