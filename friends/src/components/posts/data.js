const postsData = [];

for (let i = 1; i <= 30; i++) {
    postsData.push({
        id: i,
        name: `사용자${i}`,
        title: `게시글 제목 ${i}`,
        content: `이것은 게시글 내용 ${i} 입니다.`,
        wdate: `2025-03-${String(i).padStart(2, '0')}`, // 2025-03-01 ~ 2025-03-30
    });
}

export default postsData;
