// 查询列表数据
const getCasesPage = (params) => {
  return $axios({
    url: '/cases/page',
    method: 'get',
    params,
  })
}

//按Id获取药品信息
const getCaseById = (params) => {
  return $axios({
    url: '/cases/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteCases = (ids) => {
  return $axios({
    url: '/cases/delete',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editCase = (params) => {
  return $axios({
    url: '/cases/edit',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addCase = (params) => {
  return $axios({
    url: '/cases/add',
    method: 'post',
    data: { ...params }
  })
}

// 批量起售禁售
const casesStatusByStatus = (params) => {
  return $axios({
    url: `/cases/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
