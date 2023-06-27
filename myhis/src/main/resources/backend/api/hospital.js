function getHospital(){
    return $axios({
        url: '/hospital/list',
        method: 'get',
    })
}
function getHospitalById(params){
    return $axios({
        url: '/hospital/edit-get',
        method: 'get',
        params
    })
}

// 修改---医院
function editHospitalFinish (params) {
    return $axios({
        url: '/hospital/edit-finish',
        method: 'put',
        data: { ...params }
    })
}


function getHospitalList (params) {
    return $axios({
        url: '/hospital/page',
        method: 'get',
        params
    })
}
function addHospital(params){
    return $axios({
        url: '/hospital/add',
        method: 'post',
        data: { ...params }
    })
}

//删除一家医院
function deleteHospital(params){
    return $axios({
        url: '/hospital/delete',
        method: 'delete',
        params,
    })
}
