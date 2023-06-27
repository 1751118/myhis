// 新增---添加用户
function addUser (params) {
    return $axios({
        url: '/user/add',
        method: 'post',
        data: { ...params }
    })
}
function getOriginalUser(params){
    return $axios({
        url: '/user/edit-get',
        method: 'get',
        params,
    })
}
// 修改---用户
function editUserFinish (params) {
    return $axios({
        url: '/user/edit-finish',
        method: 'put',
        data: { ...params }
    })
}

function deleteUser(params){
    return $axios({
        url: '/user/delete',
        method: 'delete',
        params,
    })
}
