// 查询列表数据
const getOperationPage = (params) => {
  return $axios({
    url: '/operation/page',
    method: 'get',
    params
  })
}

//按Id获取手术信息
const getOperationById = (params) => {
  return $axios({
    url: '/operation/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteOperation = (ids) => {
  return $axios({
    url: '/operation/delete',
    method: 'delete',
    params: { ids }
  })
}
// 新增数据接口
const addOperation = (params) => {
  return $axios({
    url: '/operation/add',
    method: 'post',
    data: { ...params }
  })
}
// 修改数据接口
const editOperation = (params) => {
  return $axios({
    url: '/operation/edit',
    method: 'put',
    data: { ...params }
  })
}

// 批量起售禁售
const operationStatusByStatus = (params) => {
  return $axios({
    url: `/operation/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
