// 查询列表数据
const getDrugPage = (params) => {
  return $axios({
    url: '/drug/page',
    method: 'get',
    params
  })
}

//按Id获取药品信息
const getDrugById = (params) => {
  return $axios({
    url: '/drug/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteDrug = (ids) => {
  return $axios({
    url: '/drug/delete',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editDrug = (params) => {
  return $axios({
    url: '/drug/edit',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addDrug = (params) => {
  return $axios({
    url: '/drug/add',
    method: 'post',
    data: { ...params }
  })
}

// 批量起售禁售
const drugStatusByStatus = (params) => {
  return $axios({
    url: `/drug/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
